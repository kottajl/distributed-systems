package sr.ice.server;

import Devices.ISmartDevices;
import Devices.SmartDeviceInfo;
import com.zeroc.Ice.Current;

import java.util.ArrayList;
import java.util.List;

public class SmartDevicesI implements ISmartDevices {

    private final List<SmartDeviceInfo> smartDevicesInfoList;

    public SmartDevicesI () {
        smartDevicesInfoList = new ArrayList<>();
    }

    @Override
    public SmartDeviceInfo[] availableDevices(Current current) {
        return smartDevicesInfoList.toArray(new SmartDeviceInfo[0]);
    }


    public void addDevice(SmartDeviceInfo deviceInfo) {
        smartDevicesInfoList.add(deviceInfo);
    }

}
