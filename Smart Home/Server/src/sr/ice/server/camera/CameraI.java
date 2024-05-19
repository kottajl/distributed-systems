package sr.ice.server.camera;

import Devices.Camera;
import Devices.ICamera;
import Devices.LensPosition;
import com.zeroc.Ice.Current;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class CameraI implements ICamera {

    protected final Camera camera;

    public CameraI(Camera camera) {
        this.camera = camera;
    }

    @Override
    public LensPosition showPosition(Current current) {
        if (current.ctx.values().size() > 0) {
            System.out.println("There are some properties in the context");
        }

        return camera.setting;
    }

    @Override
    public String[] showImage(Current current) {
        if (current.ctx.values().size() > 0) {
            System.out.println("There are some properties in the context");
        }

        int xSize, ySize = 0;
        StringBuilder imageRow = new StringBuilder();

        try {
            //File imageData = new File("/Users/kottajl/Desktop/Rozprochy/lab4/SmartHome/Server/src/sr/ice/server/camera/image.txt");
            File imageData = new File("images/image.txt");
            Scanner reader = new Scanner(imageData);

            while (reader.hasNextLine()) {
                imageRow.append(reader.nextLine()).append('\n');
                ySize++;
            }
            reader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        String[] image = imageRow.toString().split("\n");
        xSize = image[0].length();
        assert ySize == image.length: "Error with ySize!";

        int zoom = switch (camera.setting.zoom) {
            case zoom1x -> 1;
            case zoom2x -> 2;
            case zoom3x -> 3;
        };

        int outputXSize = xSize / zoom;
        int outputYSize = ySize / zoom;

        int xLeft = Math.max(0, (xSize / 2) - (outputXSize / 2));
        xLeft = Math.max(0, xLeft + (xSize / 2) * camera.setting.transX / 100);
        if (xLeft + outputXSize >= xSize)
            xLeft = Math.max(0, xSize - outputXSize);

        int yUpper = Math.max(0, (ySize / 2) - (outputYSize / 2));
        yUpper = Math.max(0, yUpper + (ySize / 2) * camera.setting.transY / 100);
        if (yUpper + outputYSize >= ySize)
            yUpper = Math.max(0, ySize - outputYSize);

        String[] output = new String[outputYSize + 2];
        output[0] = "-".repeat(outputXSize + 2);
        for (int i=1; i<=outputYSize; i++)
            output[i] = "|" + image[yUpper + i - 1].substring(xLeft, xLeft + outputXSize) + "|";
        output[outputYSize + 1] = "-".repeat(outputXSize + 2);
        return output;
    }
}
