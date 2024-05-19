using System.Diagnostics;
using Devices;

namespace Client {
    public class CameraDevice : SmartDevice {
        
        protected virtual ICameraPrx CameraPrx { get; }

        public CameraDevice (ICameraPrx cameraPrx, string name) : base(name) {
            this.CameraPrx = cameraPrx;
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
                default: {
                    Console.WriteLine("???");
                    break;
                }
            }
        }

        protected static string GetZoomStr(Zoom zoom) {
            return zoom switch {
                Zoom.zoom1x => "x1",
                Zoom.zoom2x => "x2",
                Zoom.zoom3x => "x3",
                _ => throw new UnreachableException(),
            };
        }

        public override void GetInfo() {
            Console.WriteLine("Available commands:");
            Console.WriteLine("  show : Shows image from the camera.");
            Console.WriteLine("  pos : Shows current position of the camera's lense.");
        }
    }
}