using Devices;


namespace Client {
    public class CameraPTZDevice : CameraDevice {
        
        protected override ICameraPTZPrx CameraPrx { get; }
        
        public CameraPTZDevice (ICameraPTZPrx cameraPTZPrx, string name) : base(cameraPTZPrx, name) {
            this.CameraPrx = cameraPTZPrx;
        }

        public override void ProcessCommand(string command) {
            switch (command) {
                case "show": {
                    string[] image = CameraPrx.showImage();
                    foreach (string imageRow in image)
                        Console.WriteLine(imageRow);
                    break;
                }
                case "pos": {
                    LensPosition lensPosition = CameraPrx.showPosition();
                    Console.WriteLine($"Position: {lensPosition.transX} x {lensPosition.transY}, zoom: {GetZoomStr(lensPosition.zoom)}.");
                    break;
                }
                case "move": {
                    int x, y;
                    try {
                        Console.Write("Horizontal (X) position: ");
                        string input = Console.ReadLine() ?? throw new FormatException();
                        x = Int32.Parse(input);
                        Console.Write("Vertical (Y) position: ");
                        input = Console.ReadLine() ?? throw new FormatException();
                        y = Int32.Parse(input);
                        CameraPrx.moveLens(x, y);
                    }
                    catch (FormatException) {
                        Console.WriteLine("Invalid format of the argument!");
                    }
                    catch (InvalidArgumentError) {
                        Console.WriteLine("Position should be from range [-100, 100]!");
                    }
                    
                    break;
                }
                case "zoom": {
                    int value;
                    try {
                        Console.Write("Level of zoom: ");
                        string input = Console.ReadLine() ?? throw new FormatException();
                        value = Int32.Parse(input);
                    }
                    catch (FormatException) {
                        Console.WriteLine("Wrong argument!");
                        break;
                    }

                    CameraPrx.changeZoom(value switch {
                        1 => Zoom.zoom1x,
                        2 => Zoom.zoom2x,
                        3 => Zoom.zoom3x,
                        _ => throw new ArgumentOutOfRangeException("Wrong argument") 
                    });
                    break;
                }

                default: {
                    Console.WriteLine("???");
                    break;
                }
            }
        }

        // private static string GetZoomStr(Zoom zoom) {
        //     return zoom switch {
        //         Zoom.zoom1x => "x1",
        //         Zoom.zoom2x => "x2",
        //         Zoom.zoom3x => "x3",
        //         _ => throw new UnreachableException(),
        //     };
        // }

        public override void GetInfo() {
            // Console.WriteLine("Available commands:");
            // Console.WriteLine("  show : Shows image from the camera.");
            // Console.WriteLine("  pos : Shows current position of the camera's lense.");
            base.GetInfo();
            Console.WriteLine("  move : Moves camera's lense into new vertical and horizontal position.");
            Console.WriteLine("  zoom : Changes camera's zoom.");
        }
        
    }
}