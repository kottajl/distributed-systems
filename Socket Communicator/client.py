import socket
import select
import struct
import sys
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

MULTICASTGROUP = "228.3.29.71"
MULTICASTPORT = 10770

def send_via_udp (sock: socket.socket, my_id: int, multicast: bool = False):
    buf = ""

    while True:
        temp = input()
        if not temp:
            break
        buf += temp + '\n'

    if not buf:
        return
    buf = buf[:-1]

    data = struct.pack("!i", my_id) + bytes(buf, 'utf-8')
    if multicast:
        sock.sendto(data, (MULTICASTGROUP, MULTICASTPORT))
    else:
        sock.sendto(data, (SERVERIP, SERVERPORT))
#send_via_udp


if __name__ == "__main__":

    if os.name == 'posix':
        os.system("stty -echoctl")
    elif os.name == "nt":   # Windows
        os.system("echo off")
        os.system("color")

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as clientTCPSocket, \
        socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as clientUDPSocket, \
        socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as clientUDPMulticastSocket:
        try:

            clientTCPSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
            clientTCPSocket.connect((SERVERIP, SERVERPORT))

            clientUDPSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

            clientUDPMulticastSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
            clientUDPMulticastSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEPORT, 1)
            clientUDPMulticastSocket.setsockopt(socket.IPPROTO_IP, socket.IP_MULTICAST_TTL, struct.pack('b', 1))
            clientUDPMulticastSocket.bind(("", MULTICASTPORT))
            
            # Add clientUDPMulticastSocket to multicast group
            multicast_group_binarry = socket.inet_aton(MULTICASTGROUP)
            mreq = struct.pack('4sL', multicast_group_binarry, socket.INADDR_ANY)
            clientUDPMulticastSocket.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)

            clientTCPSocket.setblocking(False)
            clientUDPSocket.setblocking(False)
            clientUDPMulticastSocket.setblocking(False)

            data = clientTCPSocket.recv(4)
            my_id = struct.unpack("!i", data)[0]
            print(f"\r{tcol.GREEN}Connected to the server: {tcol.ENDC}I am client {tcol.MAGENTA}{my_id}{tcol.ENDC}.")
            clientUDPSocket.sendto(data + bytes("/Buenos dias", 'utf-8'), (SERVERIP, SERVERPORT))

            while True:
                events, _, _ = select.select([sys.stdin, clientTCPSocket, clientUDPSocket, clientUDPMulticastSocket], [], [])
                for event in events:

                    if event is sys.stdin:
                        msg = input()
                        if msg == "U":
                            send_via_udp(sock=clientUDPSocket, my_id=my_id)
                        elif msg == "M":
                            send_via_udp(sock=clientUDPMulticastSocket, my_id=my_id, multicast=True)
                        else:
                            clientTCPSocket.sendall(bytes(msg, 'utf-8'))

                    elif event is clientTCPSocket:
                        data = clientTCPSocket.recv(4)
                        sender_id = ...
                        if data:
                            sender_id = struct.unpack("!i", data)[0]
                        else:
                            print(f"\r{tcol.YELLOW}Connection lost!{tcol.ENDC}")
                            clientTCPSocket.close()
                            sys.exit()

                        data = clientTCPSocket.recv(1024)
                        msg = str(data, 'utf-8')
                        print(f"{tcol.BLUE}Client {sender_id}: {tcol.ENDC}{msg}")
                    
                    elif event is clientUDPSocket or event is clientUDPMulticastSocket:
                        buff, _ = event.recvfrom(2048)
                        sender_id = struct.unpack("!i", buff[:4])[0]
                        if sender_id == my_id:
                            continue
                        print(f"{tcol.BLUE}Client {sender_id}:{tcol.ENDC}")
                        print(str(buff[4:], 'utf-8'))

        
        except KeyboardInterrupt:
            print("\rClosing the client...")
    
#__main__