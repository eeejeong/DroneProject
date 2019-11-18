package kosa.team4.drone.main;

import kosa.team4.drone.network.NetworkConfig;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import syk.drone.device.Device;
import syk.drone.device.FlightController;


public class SimulationMain {
    private static final Logger logger = LoggerFactory.getLogger(SimulationMain.class);
    public static void main(String[] args) {
        NetworkConfig networkConfig = new NetworkConfig();

        FlightController flightController = new FlightController();
        flightController.mavlinkConnectTcp("localhost", 5760);
        flightController.mqttConnect(
                networkConfig.mqttBrokerConnStr,
                networkConfig.droneTopic +"/fc/pub",
                networkConfig.droneTopic +"/fc/sub"
        );


       // ElectroMagnet electroMagnet = new ElectroMagnet();
        flightController.addDevice(new Device(1) {
            @Override
            public void off() {
                logger.info("Medicine Dettach");
                //electroMagnet.off();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("msgid", "MISSION_ACTION");
                String json = jsonObject.toString();
                try {
                    flightController.mqttClient.publish("/drone/request/sub", json.getBytes(), 0, false);
                    System.out.println(json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
