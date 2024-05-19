
#ifndef DEV_ICE
#define DEV_ICE

module Devices
{
  exception GeneralError { string reason; };
  exception InvalidArgumentError extends GeneralError {};
  exception DeviceInWorkError extends GeneralError {};

  struct SmartDeviceInfo {
    string name;
    string category;
  }
  sequence<SmartDeviceInfo> SmartDevicesInfo;

  interface ISmartDevices {
    SmartDevicesInfo availableDevices();
  }


  // KETTLE

  exception WaterTooHotError extends InvalidArgumentError {};
  exception NotEnoughWaterError extends GeneralError {};

  class Kettle {
    int capacity;
    int waterContent;
    int tempereture;
  }
  interface IKettle {
    int getAmountOfWater();
    void replenishWater(int amount) throws InvalidArgumentError, DeviceInWorkError;
    idempotent void empty() throws DeviceInWorkError;

    int getTemperature();
    idempotent void heatUp(int targetTemperature) throws InvalidArgumentError, DeviceInWorkError, NotEnoughWaterError;
    idempotent void boil() throws DeviceInWorkError, NotEnoughWaterError;
  }


  // FRIDGE

  dictionary<string, int> ItemCounter;
  sequence<string> Products;

  class Fridge {
    ItemCounter content;
    int temperature;
  }
  interface IFridge {
    int getTemperature();
    idempotent void setTemperature(int targetTemperature) throws InvalidArgumentError;
    void addItems(Products products);
    ItemCounter getContent();
  }
  

  // CAMERAS

  enum Zoom { zoom1x, zoom2x, zoom3x };

  struct LensPosition {
    Zoom zoom;
    int transX;
    int transY;
  }
  sequence<string> Image;

  class Camera {
    LensPosition setting;
  }
  interface ICamera {
    LensPosition showPosition();
    Image showImage();
  }
  interface ICameraPTZ extends ICamera {
    idempotent void changeZoom(Zoom zoom);
    idempotent void moveLens(int transX, int transY) throws InvalidArgumentError;
  }

};

#endif
