using Devices;

namespace Client {
    public class FridgeDevice : SmartDevice {
        
        private readonly IFridgePrx fridgePrx;
        
        public FridgeDevice (IFridgePrx fridgePrx, string name) : base(name) {
            this.fridgePrx = fridgePrx;
        }

        public override void ProcessCommand(string command) {
            switch (command) {
                case "add": {
                    string[] products;
                    Console.Write("Products to add: ");
                    try {
                        string input = Console.ReadLine() ?? throw new FormatException();
                        products = input.Split(' ');
                        fridgePrx.addItems(products);
                    }
                    catch (FormatException) {
                        Console.WriteLine("Invalid format of the arguments!");
                    }
                    break;
                }
                case "show": {
                    Dictionary<string, int> products;
                    products = fridgePrx.getContent();
                    if (products.Count > 0)
                        foreach (string product in products.Keys)
                            Console.WriteLine($"{product} x{products[product]}");
                    else
                        Console.WriteLine("The fridge is empty.");
                    break;
                }
                case "get-temp": {
                    int value = fridgePrx.getTemperature();
                    Console.WriteLine($"Current temperature: {value} [℃].");
                    break;
                }
                case "set-temp": {
                    Console.Write("Target temperature (in ℃): ");
                    int value;
                    try {
                        string input = Console.ReadLine() ?? throw new FormatException();
                        value = Int32.Parse(input);
                        fridgePrx.setTemperature(value);
                    }
                    catch (FormatException) {
                        Console.WriteLine("Invalid format of the argument!");
                    }
                    catch (InvalidArgumentError) {
                        Console.WriteLine("Temperature should be from range [1-7] ℃");
                    }
                    
                    break;
                }
                default:
                    Console.WriteLine("???");
                    break;
            }
        }

        public override void GetInfo() {
            Console.WriteLine("Available commands:");
            Console.WriteLine("  add : Adds passed products to the fridge.");
            Console.WriteLine("  show : Shows current content of the fridge.");
            Console.WriteLine("  get-temp : Shows current temperature inside the fridge.");
            Console.WriteLine("  set-temp : Sets new target temperature.");
        }

    }
}