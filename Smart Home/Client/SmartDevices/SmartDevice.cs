using System;
using System.Collections;
using System.Diagnostics;
using Devices;

namespace Client {
    public abstract class SmartDevice {

        readonly string name;

        public SmartDevice() {
            name = "anonymous-device";
        }
        public SmartDevice(string name) {
            this.name = name;
        }

        public void StartCommandLoop() {
            GetInfo();
            Console.WriteLine("Type \'exit\' to go back to other devices.");
            do {
                Console.Write("==> ");
                string input = Console.ReadLine() ?? throw new UnreachableException();

                switch (input) {
                    case "name":
                        Console.WriteLine($"Name of the device: {name}");
                        break;
                    case "help":
                        GetInfo();
                        Console.WriteLine("Type \'exit\' to go back to other devices.");
                        break;
                    case "exit":
                        return;
                    case "x":
                    case "":
                        break;
                    default:
                        ProcessCommand(input);
                        break;
                }
            }
            while (true);
        }

        public abstract void ProcessCommand(string command);

        public abstract void GetInfo();

    }
}