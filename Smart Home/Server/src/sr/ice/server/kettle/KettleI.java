package sr.ice.server.kettle;

import Devices.*;
import com.zeroc.Ice.Current;

import java.time.Duration;
import java.time.LocalDateTime;

public class KettleI implements IKettle {
    private final Kettle kettle;
    LocalDateTime lastHeatingUp;
    boolean inWork;

    public KettleI(Kettle kettle) {
        this.kettle = kettle;
        lastHeatingUp = null;
        inWork = false;
    }

    @Override
    public int getAmountOfWater(Current current) {
        if (current.ctx.values().size() > 0) {
            System.out.println("There are some properties in the context");
        }

        return kettle.waterContent;
    }

    @Override
    public void replenishWater(int amount, Current current) throws InvalidArgumentError, DeviceInWorkError {
        if (current.ctx.values().size() > 0) {
            System.out.println("There are some properties in the context");
        }

        if (amount <= 0 || kettle.waterContent + amount > kettle.capacity)
            throw new InvalidArgumentError(String.valueOf(kettle.capacity - kettle.waterContent));

        if (inWork)
            throw new DeviceInWorkError();

        int targetLevel = Math.min(amount + kettle.waterContent, kettle.capacity);

        try {
            inWork = true;
            while (kettle.waterContent < targetLevel) {
                kettle.waterContent = Math.min(kettle.waterContent + 50, targetLevel);
                Thread.sleep(500);
            }
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        finally {
            inWork = false;
        }
    }

    @Override
    public void empty(Current current) throws DeviceInWorkError {
        if (current.ctx.values().size() > 0) {
            System.out.println("There are some properties in the context");
        }

        if (inWork)
            throw new DeviceInWorkError();

        try {
            inWork = true;
            while (kettle.waterContent > 0) {
                kettle.waterContent = Math.max(kettle.waterContent - 50, 0);
                Thread.sleep(500);
            }
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        finally {
            inWork = false;
            kettle.tempereture = 25;
        }
    }

    private void updateTemperature() {
        LocalDateTime nowDate = LocalDateTime.now();
        if (lastHeatingUp != null) {
            Duration timePassed = Duration.between(lastHeatingUp, nowDate);
            int diff = (int) Math.min(timePassed.toSeconds(), 100);

            kettle.tempereture = Math.max(25, kettle.tempereture - diff);
        }
    }

    @Override
    public int getTemperature(Current current) {
        if (current.ctx.values().size() > 0) {
            System.out.println("There are some properties in the context");
        }

        updateTemperature();
        return kettle.tempereture;
    }

    @Override
    public void heatUp(int targetTemperature, Current current) throws InvalidArgumentError, DeviceInWorkError, NotEnoughWaterError {
        if (current.ctx.values().size() > 0) {
            System.out.println("There are some properties in the context");
        }

        updateTemperature();

        if (targetTemperature <= 25 || targetTemperature > 100)
            throw new InvalidArgumentError();

        if (inWork)
            throw new DeviceInWorkError();

        if (targetTemperature <= kettle.tempereture)
            throw new WaterTooHotError();

        if (kettle.waterContent < 300)
            throw new NotEnoughWaterError();


        try {
            inWork = true;
            while (kettle.tempereture < targetTemperature) {
                kettle.tempereture = Math.min(kettle.tempereture + 5, targetTemperature);
                Thread.sleep(1000);
            }
            lastHeatingUp = LocalDateTime.now();
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        finally {
            inWork = false;
        }
    }

    @Override
    public void boil(Current current) throws DeviceInWorkError, NotEnoughWaterError {
        try {
            this.heatUp(100, current);
        }
        catch (InvalidArgumentError ex) {
            System.out.println("Kettle.boil(): Unreachable code happened...");
            System.out.println("More info: https://xkcd.com/2200/");
        }
    }
}
