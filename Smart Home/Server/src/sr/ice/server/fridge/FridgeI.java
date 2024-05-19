package sr.ice.server.fridge;

import Devices.Fridge;
import Devices.IFridge;
import Devices.InvalidArgumentError;
import com.zeroc.Ice.Current;

import java.util.Map;

public class FridgeI implements IFridge {

    private final Fridge fridge;

    public FridgeI(Fridge fridge) {
        this.fridge = fridge;
    }

    @Override
    public int getTemperature(Current current) {
        if (current.ctx.values().size() > 0) {
            System.out.println("There are some properties in the context");
        }

        return fridge.temperature;
    }

    @Override
    public void setTemperature(int targetTemperature, Current current) throws InvalidArgumentError {
        if (current.ctx.values().size() > 0) {
            System.out.println("There are some properties in the context");
        }

        if (targetTemperature <= 0 || targetTemperature > 7)
            throw new InvalidArgumentError();

        fridge.temperature = targetTemperature;
    }

    @Override
    public void addItems(String[] products, Current current) {
        if (current.ctx.values().size() > 0) {
            System.out.println("There are some properties in the context");
        }

        for (String productName: products) {
            productName = productName.substring(0, 1).toUpperCase() + productName.substring(1).toLowerCase();
            fridge.content.merge(productName, 1, Integer::sum);
        }
    }

    @Override
    public Map<String, Integer> getContent(Current current) {
        if (current.ctx.values().size() > 0) {
            System.out.println("There are some properties in the context");
        }

        return fridge.content;
    }
}
