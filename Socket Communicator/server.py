import socket
import struct
from threading import Thread, Lock
import os

class tcol:
    FAIL = '\033[91m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    BLUE = '\033[94m'
    MAGENTA = '\033[95m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
#tcol

SERVERIP = "127.0.0.1"
SERVERPORT = 37540

threads = []
clients = {}
clients_lock = Lock()

class UDPThread(Thread):
    def __init__(self, sock: socket.socket):
        super().__init__()
        self._sock = sock
    #__init__
    
    def run(self) -> None:
        with self._sock:
            while True:
                try:
                    data, sender = self._sock.recvfrom(2048)
                except Exception:
                    break
                
                sender_id = struct.unpack("!i", data[:4])[0]

                if len(data) == 16 and str(data[4:16], 'utf-8') == "/Buenos dias":
                    with clients_lock:
                        clients[sender_id]['udp'] = sender
                        continue

                with clients_lock:
                    other_clients = list(filter(lambda x: x[0] is not sender_id, clients.items()))
                    for _, other_client in other_clients:
                        self._sock.sendto(data, other_client['udp'])
    #run
#UDPThread
    
class TCPThread(Thread):
    def __init__(self, client_id: int):
        super().__init__()
        self._client_id = client_id
    #__init__
    
    def run(self) -> None:
        client_socket = ...

        with clients_lock:
            client_socket = clients[self._client_id]['tcp']['socket']
        
        with client_socket:
            while True:
                try:
                    data = client_socket.recv(1024)
                except Exception:
                    break
                if not data:
                    break

                with clients_lock:
                    other_clients = list(filter(lambda x: x[0] is not self._client_id, clients.items()))
                    for _, other_client in other_clients:
                        other_client['tcp']['socket'].sendall(struct.pack("!i", int(self._client_id)) + data)
        
        print(f"{tcol.YELLOW}Connection with client {self._client_id} lost.{tcol.ENDC}")
        with clients_lock:
            del clients[self._client_id]
    #run
#TCPThread


if __name__ == "__main__":

    if os.name == 'posix':
        os.system("stty -echoctl")
    elif os.name == "nt":   # Windows
        os.system("echo off")
        os.system("color")

    print("Starting the server...")
    next_client_id = 1

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as serverTCPSocket, \
        socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as serverUDPSocket:
        try:
            serverTCPSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
            serverTCPSocket.bind((SERVERIP, SERVERPORT))
            serverTCPSocket.listen(5)

            serverUDPSocket.bind((SERVERIP, SERVERPORT))
            udp_thread = UDPThread(sock=serverUDPSocket)
            udp_thread.setDaemon(True)
            udp_thread.start()

            while True:
                client_socket, client_address = serverTCPSocket.accept()
                if not client_socket:
                    continue

                new_client_id = next_client_id
                next_client_id += 1
                print(f"{tcol.GREEN}Connection with new client (address: {client_address}, id: {new_client_id}).{tcol.ENDC}")
                client_socket.sendall(struct.pack("!i", int(new_client_id)))
                
                with clients_lock:
                    clients[new_client_id] = {'tcp': {'socket': client_socket, 'address': client_address}, 'udp': ()}
                
                new_thread = TCPThread(client_id=new_client_id)
                new_thread.setDaemon(True)
                threads.append(new_thread)
                new_thread.start()

        except KeyboardInterrupt:
            print("Closing the server...")
#__main__