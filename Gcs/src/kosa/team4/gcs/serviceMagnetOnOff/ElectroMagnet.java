package kosa.team4.gcs.serviceMagnetOnOff;

import kosa.team4.gcs.network.NetworkConfig;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;

public class ElectroMagnet {
    private MqttClient mqttClient;
    private String pubTopic = NetworkConfig.instance.droneTopic + "/magnet/sub";
    private String subTopic = NetworkConfig.instance.droneTopic + "/magnet/pub";

    public ElectroMagnet() throws Exception {
        mqttClient = new MqttClient(NetworkConfig.instance.mqttBrokerConnStr, MqttClient.generateClientId(), null);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setConnectionTimeout(5);
        options.setAutomaticReconnect(true);
        mqttClient.connect(options);
        mqttReceiveFromGcs();
    }


    public void magnetOn() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action", "on");
            String json = jsonObject.toString();
            mqttClient.publish(pubTopic, json.getBytes(), 0, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void magnetOff() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action", "off");
            String json = jsonObject.toString();
            mqttClient.publish(pubTopic, json.getBytes(), 0, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mqttReceiveFromGcs() {
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                System.out.println("Arrive Success");
                String json = new String(mqttMessage.getPayload()); //{"action":"on"}
                System.out.println(json);
                JSONObject jsonObject = new JSONObject(json);
                String status = jsonObject.getString("status");
                System.out.println(status);
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            }
            @Override
            public void connectionLost(Throwable throwable) {
            }
        });

        try {
            mqttClient.subscribe(subTopic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
