package sr.ice.server.camera;

import Devices.Camera;
import Devices.ICameraPTZ;
import Devices.InvalidArgumentError;
import Devices.Zoom;
import com.zeroc.Ice.Current;

public class CameraPTZI extends CameraI implements ICameraPTZ {

    public CameraPTZI(Camera camera) {
        super(camera);
    }

    @Override
    public void changeZoom(Zoom zoom, Current current) {
        if (current.ctx.values().size() > 0) {
            System.out.println("There are some properties in the context");
        }

        camera.setting.zoom = zoom;
    }

    @Override
    public void moveLens(int transX, int transY, Current current) throws InvalidArgumentError {
        if (current.ctx.values().size() > 0) {
            System.out.println("There are some properties in the context");
        }

        if ((transX < -100 || transX > 100) || (transY < -100 || transY > 100)) {
            throw new InvalidArgumentError();
        }

        camera.setting.transX = transX;
        camera.setting.transY = transY;
    }
}
