/*
java -Djava.library.path=/usr/lib/jni:/home/pi/opencv/opencv-3.4.5/build/lib -cp classes:lib/'*' companion.companion.RealMain
 */

package kosa.team4.drone.main;

import kosa.team4.drone.network.NetworkConfig;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import syk.drone.device.Camera;
import syk.drone.device.Device;
import syk.drone.device.FlightController;


public class RealMain {
    private static final Logger logger = LoggerFactory.getLogger(RealMain.class);
    public static void main(String[] args) {
        NetworkConfig networkConfig = new NetworkConfig();

        FlightController flightController = new FlightController();
        flightController.mavlinkConnectRxTx("/dev/ttyAMA0");
        flightController.mqttConnect(
                networkConfig.mqttBrokerConnStr,
                networkConfig.droneTopic +"/fc/pub",
                networkConfig.droneTopic +"/fc/sub"
        );
        // 전방 카메라
        Camera camera0 = new Camera();
        camera0.cameraConnect(0, 320, 240, 0);
        camera0.mattConnect(
                networkConfig.mqttBrokerConnStr,
                networkConfig.droneTopic + "/cam0/pub",
                networkConfig.droneTopic + "/cam0/sub"
        );

        // 하방 카메라
        Camera camera1 = new Camera();
        camera1.cameraConnect(1, 320, 240, 0);
        camera1.mattConnect(
                networkConfig.mqttBrokerConnStr,
                networkConfig.droneTopic +"/cam1/pub",
                networkConfig.droneTopic +"/cam1/sub"
        );

        ElectroMagnet electroMagnet = new ElectroMagnet();
        electroMagnet.mattConnect(
                networkConfig.mqttBrokerConnStr,
                networkConfig.droneTopic +"/magnet/pub",
                networkConfig.droneTopic +"/magnet/sub"
        );

        flightController.addDevice(new Device(1) {
            @Override
            public void off() {
                logger.info("Medicine Dettach");
                electroMagnet.off();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("msgid", "MISSION_ACTION");
                String json = jsonObject.toString();
                try {
                    electroMagnet.mqttClient.publish("/drone/request/sub", json.getBytes(), 0, false);
                    System.out.println(json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
