package sr.ice.server;

import Devices.*;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Identity;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import sr.ice.server.camera.CameraPTZI;
import sr.ice.server.camera.CameraI;
import sr.ice.server.fridge.FridgeI;
import sr.ice.server.kettle.KettleI;

import java.util.HashMap;
import java.util.Map;

public class IceServer {
	public void t1(String[] args) {
		int status = 0;
		Communicator communicator = null;

		try {
			// 1. Inicjalizacja ICE - utworzenie communicatora
			communicator = Util.initialize(args);

			String port = null;
			try {
				port = args[1];
			}
			catch (IndexOutOfBoundsException ex) {
				System.out.println("Missing argument!");
				System.exit(1);
			}
			assert port != null: "Something wrong with the port argument!";

			// 2. Konfiguracja adaptera
			// METODA 1 (polecana produkcyjnie): Konfiguracja adaptera Adapter1 jest w pliku konfiguracyjnym podanym jako parametr uruchomienia serwera
			//ObjectAdapter adapter = communicator.createObjectAdapter("Adapter1");

			// METODA 2 (niepolecana, dopuszczalna testowo): Konfiguracja adaptera Adapter1 jest w kodzie źródłowym
			System.out.println("Port: " + port);
			ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("Adapter1", "tcp -h 127.0.0.1 -p "
					+ port + " -z : udp -h 127.0.0.1 -p " + port + " -z");

			// 3. Utworzenie serwanta/serwantów
			SmartDevicesI smartDevices = new SmartDevicesI();
			adapter.add(smartDevices, new Identity("devices", "devices"));

			String input = null;
			try {
				input = args[2];
			}
			catch (IndexOutOfBoundsException ex) {
				System.out.println("Missing argument!");
				System.exit(1);
			}
			assert input != null: "Something wrong with the servants argument!";

			Map<Character, Integer> servantsNextNum = new HashMap<>();
			servantsNextNum.put('k', 1);
			servantsNextNum.put('f', 1);
			servantsNextNum.put('c', 1);
			servantsNextNum.put('p', 1);

			for (char c: input.toCharArray()) {
				int num = servantsNextNum.get(c);
				servantsNextNum.put(c, num + 1);

				switch (c) {
					case 'k':
						KettleI kettleServant = new KettleI(new Kettle(1500, 0, 25));
						adapter.add(kettleServant, new Identity("kettle" + num, "kettle"));
						smartDevices.addDevice(new SmartDeviceInfo("kettle" + num, "kettle"));
						break;
					case 'f':
						FridgeI fridgeServant = new FridgeI(new Fridge(new HashMap<String, Integer>(), 4));
						adapter.add(fridgeServant, new Identity("fridge" + num, "fridge"));
						smartDevices.addDevice(new SmartDeviceInfo("fridge" + num, "fridge"));
						break;
					case 'c':
						CameraI cameraServant = new CameraI(new Camera(new LensPosition(Zoom.zoom1x, 0, 0)));
						adapter.add(cameraServant, new Identity("camera" + num, "camera"));
						smartDevices.addDevice(new SmartDeviceInfo("camera" + num, "camera"));
						break;
					case 'p':
						CameraPTZI cameraPTZServant = new CameraPTZI(new Camera(new LensPosition(Zoom.zoom1x, 0, 0)));
						adapter.add(cameraPTZServant, new Identity("cameraPTZ" + num, "cameraPTZ"));
						smartDevices.addDevice(new SmartDeviceInfo("cameraPTZ" + num, "cameraPTZ"));
						break;
				}
			}

			// 5. Aktywacja adaptera i wejście w pętlę przetwarzania żądań
			adapter.activate();

			System.out.println("Entering event processing loop...");

			communicator.waitForShutdown();

		} catch (Exception e) {
			e.printStackTrace(System.err);
			status = 1;
		}
		if (communicator != null) {
			try {
				communicator.destroy();
			} catch (Exception e) {
				e.printStackTrace(System.err);
				status = 1;
			}
		}
		System.exit(status);
	}


	public static void main(String[] args) {
		IceServer app = new IceServer();
		app.t1(args);
	}
}