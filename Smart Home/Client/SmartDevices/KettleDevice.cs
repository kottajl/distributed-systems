using Devices;

namespace Client {
    public class KettleDevice : SmartDevice {
        
        private readonly IKettlePrx kettlePrx;
        

        public KettleDevice (IKettlePrx kettlePrx, string name) : base(name) {
            this.kettlePrx = kettlePrx;
        }

        public override void ProcessCommand(string command) {
            switch (command) {
                case "get-temp": {
                    int value = kettlePrx.getTemperature();
                    Console.WriteLine($"Temperature of water in kettle: {value} [*C]");
                    break;
                }
                case "get-amount": {
                    int value = kettlePrx.getAmountOfWater();
                    Console.WriteLine($"Amount of water in kettle: {value} ml");
                    break;
                }
                case "add-water": {
                    Console.Write("Amount of water to replenish (in ml): ");
                    int value;
                    try {
                        string input = Console.ReadLine() ?? throw new FormatException();
                        value = Int32.Parse(input);
                        kettlePrx.replenishWaterAsync(value).ContinueWith(task => {
                            try {
                                if (task.Exception != null)
                                    throw task.Exception.InnerException ?? task.Exception;
                                Console.WriteLine("Water replenished!");
                            }
                            catch (InvalidArgumentError ex) {
                                Console.WriteLine($"Amount of water should be from range [1, {ex.reason}]");
                            }
                            catch (DeviceInWorkError) {
                                Console.WriteLine("Device is already in work. Please try again later.");
                            }
                        });
                    }
                    catch (FormatException) {
                        Console.WriteLine("Invalid format of the argument!");
                        break;
                    }
                    break;
                }
                case "empty": {
                    kettlePrx.emptyAsync().ContinueWith(task => {
                        try {
                            if (task.Exception != null)
                                throw task.Exception.InnerException ?? task.Exception;
                            Console.WriteLine("Kettle has been emptied!");
                        }
                        catch (DeviceInWorkError) {
                            Console.WriteLine("Device is already in work. Please try again later.");
                        }
                    }); 
                    break;
                }
                case "heat-up": {
                    Console.Write("Desired temperature: ");
                    int value;
                    try {
                        string input = Console.ReadLine() ?? throw new FormatException();
                        value = Int32.Parse(input);
                        kettlePrx.heatUpAsync(value).ContinueWith(task => {
                            try {
                                if (task.Exception != null)
                                    throw task.Exception.InnerException ?? task.Exception;
                                Console.WriteLine("Water has been heated up!");
                            }
                            catch (WaterTooHotError) {
                                Console.WriteLine("Water is already hotter than this.");
                            }
                            catch (InvalidArgumentError) {
                                Console.WriteLine("Temperature should be from range [26-100].");
                            }
                            catch (DeviceInWorkError) {
                                Console.WriteLine("Device is already in work. Please try again later.");
                            }
                            catch (NotEnoughWaterError) {
                                Console.WriteLine("There is not enough water in the kettle.");
                            }
                        });
                    }
                    catch (FormatException) {
                        Console.WriteLine("Invalid format of the argument!");
                    }
                    break;
                }
                case "boil": {
                    kettlePrx.boilAsync().ContinueWith(task => {
                        try {
                            if (task.Exception != null)
                                throw task.Exception.InnerException ?? task.Exception;
                            Console.WriteLine("Water has been boiled!");
                        }
                        catch (DeviceInWorkError) {
                            Console.WriteLine("Device is already in work. Please try again later.");
                        }
                        catch (NotEnoughWaterError) {
                            Console.WriteLine("There is not enough water in the kettle.");
                        }  
                    });
                    break;
                }
                default:
                    Console.WriteLine("???");
                    break;
            }
        }

        public override void GetInfo() {
            Console.WriteLine("Available commands:");
            Console.WriteLine("  get-temp : Shows current temperature of water.");
            Console.WriteLine("  get-amount : Shows current amount of water.");
            Console.WriteLine("  add-water : Pours passed amount of water to the kettle.");
            Console.WriteLine("  empty : Empties the kettle.");
            Console.WriteLine("  heat-up : Starts heating up water to desired temperature.");
            Console.WriteLine("  boil : Starts boiling water.");
        }
    }
}