from kazoo.client import KazooClient
from kazoo.protocol.states import WatchedEvent
from collections import deque
import sys, os
import subprocess
import random


class tcol:
    FAIL = '\033[91m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    BLUE = '\033[94m'
    MAGENTA = '\033[95m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
#tcol


assert len(sys.argv) > 2, "Wrong number of parameters!"
PORT = sys.argv[1]
APP_PATH = sys.argv[2]


app_process = None

zk = KazooClient(hosts=f"127.0.0.1:{PORT}")
old_children = {}
connected = False


@zk.DataWatch("/a")
def watch_node(data, stat, event):
    if event is not None and isinstance(event, WatchedEvent):
        global app_process

        if event.type == "CREATED":
            print(f"{tcol.BLUE}Created node /a.{tcol.ENDC}")
            old_children["/a"] = []
            start_watch_children("/a")
            try:
                app_process = subprocess.Popen([APP_PATH])
            except FileNotFoundError:
                print(f"{tcol.FAIL}Error: File not found!{tcol.ENDC}")
            except PermissionError:
                print(f"{tcol.FAIL}Error: No permission to open file!{tcol.ENDC}")

        elif event.type == "DELETED":
            print(f"{tcol.BLUE}Deleted node /a.{tcol.ENDC}")
            if app_process is not None and app_process.poll() is None:      # if process is working
                app_process.terminate()
                try:
                    app_process.wait(timeout=1)
                except subprocess.TimeoutExpired:
                    app_process.kill()
                app_process = None
#watch_node


# Setting children watcher on passed node.
def start_watch_children (node: str) -> None:

    @zk.ChildrenWatch(node)
    def watch_children(children):
        global old_children

        new_children = set(children) - set(old_children[node])

        if len(new_children) > 0:                                               # adding children
            print()
            print(f'{tcol.BOLD}Num of all children (in-depth) of "/a":{tcol.ENDC} {dfs_tree("/a") - 1}')
            print(f'{tcol.BOLD}Num of children of "/a":{tcol.ENDC} {len(zk.get_children("/a"))}')
            if node != "/a":
                print(f'{tcol.BOLD}Num of children of "{node}":{tcol.ENDC} {len(children)}')

        for child in new_children:
            child_key = node + "/" + child
            old_children[child_key] = []
            start_watch_children(child_key)

        old_children[node] = children
    #watch_children

#start_watch_children


# Performing DFS on tree with passed root (with optional drawing), returning number of all nodes in the tree.
def dfs_tree (root: str, depth: int = 0, draw: bool = False) -> int:
    prefix = " " * (2 * (depth - 1)) + " |- " if depth != 0 else ""
    begin_it = root.rfind('/')

    if draw:
        print(f"{prefix}{tcol.GREEN}{root[begin_it:]}{tcol.ENDC}")

    children = zk.get_children(root)
    children_sum = 0
    for child in children:
        children_sum += dfs_tree(root + '/' + child, depth + 1, draw=draw)
    
    return children_sum + 1
#draw_tree_from_root


# Drawing whole tree (in-depth) of "/a" node.
def draw_tree () -> None:
    if not zk.exists("/a"):
        print("Node does not exist!")
        return
    dfs_tree("/a", draw=True)
#draw_tree


# Setting watchers on every node in "/a" tree.
def init_nodes () -> None:
    Q = deque()

    if zk.exists("/a"):
        Q.append("/a")
    
    while len(Q) > 0:
        node = Q.popleft()

        old_children[node] = zk.get_children(node)
        for child_node in old_children[node]:
            Q.append(node + "/" + child_node)

        start_watch_children(node)
#init_nodes




if __name__ == "__main__":

    if os.name == 'posix':
        os.system("stty -echoctl")
    elif os.name == "nt":   # Windows
        os.system("echo off")
        os.system("color")
    
    zk.start()
    init_nodes()
    
    # First messages to user
    print(tcol.YELLOW)
    print(random.choice(["Hello!", "Willkommen!", "Salut!", "Hola!"]))
    print('Type "show" to show the tree structure of "/a" node.')
    print('Type "exit" / "quit" / "q" to exit the client.')
    print(tcol.ENDC)

    # User input
    while True:
        print("> ", end="", sep="")
        command = input()

        if (command in ["exit", "quit", "q"]):
            break

        elif (command == "show"):
            draw_tree()

        elif (command in ["", " "]):
            continue

        else:
            print("???")
        
    zk.stop()
#__main__