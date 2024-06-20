from kazoo.client import KazooClient
from kazoo.protocol.states import WatchedEvent, KazooState
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


APP_PATH = ""
app_process = None


zk = KazooClient(hosts="127.0.0.1:2181")
old_children = []
connected = False


@zk.DataWatch("/a")
def watch_node(data, stat, event):
    if event is not None and isinstance(event, WatchedEvent):
        global app_process

        if event.type == "CREATED":
            print("Created node /a.")
            start_watch_children()
            try:
                app_process = subprocess.Popen([APP_PATH])
            except FileNotFoundError:
                print("Error: File not found!")
            except PermissionError:
                print("Error: No permission to open file!")

        elif event.type == "DELETED":
            print("Deleted node /a.")
            if app_process is not None and app_process.poll() is None:      # if process is working
                app_process.terminate()
                try:
                    app_process.wait(timeout=1)
                except subprocess.TimeoutExpired:
                    app_process.kill()
                app_process = None

#watch_node


def start_watch_children () -> None:

    @zk.ChildrenWatch("/a")
    def watch_children(children):
        global old_children

        new_children = set(children) - set(old_children)

        if len(new_children) > 0:
            print(f"Num of children: {len(children)}")

        old_children = children
    #watch_children

#start_watch_children


def draw_tree_from_root (root: str, depth: int = 0) -> None:
    prefix = " " * (2 * (depth - 1)) + " |- " if depth != 0 else ""
    begin_it = root.rfind('/')

    print(f"{prefix}{tcol.GREEN}{root[begin_it:]}{tcol.ENDC}")

    children = zk.get_children(root)
    for child in children:
        draw_tree_from_root(root + '/' + child, depth + 1)
#draw_tree_from_root

def draw_tree () -> None:
    if not zk.exists("/a"):
        print("Node does not exist!")
        return
    draw_tree_from_root("/a")
#draw_tree


if __name__ == "__main__":

    if os.name == 'posix':
        os.system("stty -echoctl")
    elif os.name == "nt":   # Windows
        os.system("echo off")
        os.system("color")

    assert len(sys.argv) > 1, "Wrong number of parameters!"

    # set APP_PATH
    APP_PATH = sys.argv[1]
    
    zk.start()
    if zk.exists("/a"):
        print("/a exists")
        old_children = zk.get_children("/a")
        start_watch_children()
    
    print()
    print(random.choice(["Hello!", "Willkommen!", "Salut!", "Hola!"]))
    print('Type "show" to show the tree structure of "/a" node.')
    print('Type "exit" / "quit" / "q" to exit the client.')
    print()

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