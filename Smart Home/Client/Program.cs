using System;
using System.Collections;
using System.Reflection.Metadata;
using System.Xml.Linq;
using Devices;
using Ice;

namespace Client {
    public class Program {
        private static void WriteDevices(in Dictionary<string, SmartDevice> deviceMap) {
            Console.WriteLine("Available devices:");
            foreach (string deviceName in deviceMap.Keys)
                Console.WriteLine($"  {deviceName}");
            Console.WriteLine("");
            Console.WriteLine("Type device's name to access its functionality.");
        }

        public static void Main(string[] args) {

            try {
                using Ice.Communicator communicator = Ice.Util.initialize(ref args);

                if (args.Length == 0) {
                    Console.Error.WriteLine("Lack of servers ports in the arguments.");
                    return;
                }
                
                Dictionary<string, SmartDevice> deviceMap = new Dictionary<string, SmartDevice>();

                int serverNumber = 1;
                foreach (string port in args) {
                    var base0 = communicator.stringToProxy($"devices/devices:tcp -h 127.0.0.1 -p {port} -z : udp -h 127.0.0.1 -p {port} -z");
                    ISmartDevicesPrx avaliableDevices = ISmartDevicesPrxHelper.checkedCast(base0);

                    if (avaliableDevices == null)
                        throw new ApplicationException("Invalid proxy");

                    // READING AVAILABLE DEVICES
                    SmartDeviceInfo[] smartDevices = avaliableDevices.availableDevices();
                    foreach (SmartDeviceInfo deviceInfo in smartDevices) {
                        ObjectPrx tempBase = communicator.stringToProxy($"{deviceInfo.category}/{deviceInfo.name}:tcp -h 127.0.0.1 -p {port} -z : udp -h 127.0.0.1 -p {port} -z");
                        string mapDeviceName = args.Length == 1 ? deviceInfo.name : $"s{serverNumber}-{deviceInfo.name}";

                        switch (deviceInfo.category) {
                            case "kettle": {
                                IKettlePrx kettlePrx = IKettlePrxHelper.checkedCast(tempBase);
                                if (avaliableDevices == null)
                                    throw new ApplicationException("Invalid proxy");
                                deviceMap[mapDeviceName] = new KettleDevice(kettlePrx, deviceInfo.name);
                                break;
                            }
                            case "fridge": {
                                IFridgePrx fridgePrx = IFridgePrxHelper.checkedCast(tempBase);
                                if (avaliableDevices == null)
                                    throw new ApplicationException("Invalid proxy");
                                deviceMap[mapDeviceName] = new FridgeDevice(fridgePrx, deviceInfo.name);
                                break;
                            }
                            case "camera": {
                                ICameraPrx cameraPrx = ICameraPrxHelper.checkedCast(tempBase);
                                if (avaliableDevices == null)
                                    throw new ApplicationException("Invalid proxy");
                                deviceMap[mapDeviceName] = new CameraDevice(cameraPrx, deviceInfo.name);
                                break;
                            }
                            case "cameraPTZ": {
                                ICameraPTZPrx cameraPTZPrx = ICameraPTZPrxHelper.checkedCast(tempBase);
                                if (avaliableDevices == null)
                                    throw new ApplicationException("Invalid proxy");
                                deviceMap[mapDeviceName] = new CameraPTZDevice(cameraPTZPrx, deviceInfo.name);
                                break;
                            }
                            default:
                                break;
                        }
                    }

                    serverNumber++;
                }

                // var base0 = communicator.stringToProxy("devices/devices:tcp -h 127.0.0.1 -p 10000 -z : udp -h 127.0.0.1 -p 10000 -z");
                // ISmartDevicesPrx avaliableDevices = ISmartDevicesPrxHelper.checkedCast(base0);

                // if (avaliableDevices == null)
                //     throw new ApplicationException("Invalid proxy");

                

                

                Console.WriteLine("Hello!");
                WriteDevices(in deviceMap);

                do
                {
                    Console.Write("> ");
                    string? input = Console.ReadLine();
                    if (input == null)
                        break;

                    if (deviceMap.ContainsKey(input))
                        deviceMap[input].StartCommandLoop();

                    else switch (input)
                        {
                            case "help":
                            case "devices":
                                WriteDevices(in deviceMap);
                                break;
                            case "exit":
                                return;
                            case "":
                            case "x":
                                break;
                            default:
                                Console.WriteLine("???");
                                break;
                        }
                }
                while (true);
            }
            catch (System.Exception ex) {
                Console.Error.WriteLine(ex);
            }

        }
    }
}