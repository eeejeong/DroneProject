package kosa.team4.drone.main;

import com.pi4j.io.gpio.*;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;

public class ElectroMagnet {
    private GpioPinDigitalOutput pin1;
    private GpioPinDigitalOutput pin2;

    private String status ="off";

    public MqttClient mqttClient;
    private String pubTopic;
    private String subTopic;

    public ElectroMagnet(){
        GpioController gpioController = GpioFactory.getInstance();
        pin1 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_24, PinState.LOW);
        pin2 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_25, PinState.LOW);
        pin1.setShutdownOptions(true, PinState.LOW);
        pin2.setShutdownOptions(true, PinState.LOW);
    }

    public void on() {
        pin1.high();
        pin2.high();
        status = "on";
    }
    public void off(){
        pin1.low();
        pin2.low();
        status = "off";
    }

    public String getStatus(){
        return status;
    }

    public void mattConnect(String mqttBrokerConnStr, String pubTopic, String subTopic) {
        this.pubTopic = pubTopic;
        this.subTopic = subTopic;

        while (true) {
            try {
                mqttClient = new MqttClient(mqttBrokerConnStr, MqttClient.generateClientId(), null);
                MqttConnectOptions options = new MqttConnectOptions();
                options.setConnectionTimeout(5);
                options.setAutomaticReconnect(true);
                mqttClient.connect(options);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                try { mqttClient.close(); } catch (Exception e1) {}
                try { Thread.sleep(1000); } catch (InterruptedException e1) {}
            }
        }

        thread.start();
        mqttReceiveFromGcs();
    }

    public void mqttReceiveFromGcs() {
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                System.out.println("Arrive Success");
                String json = new String(mqttMessage.getPayload()); //{"action":"on"}
                JSONObject jsonObject = new JSONObject(json);
                String action = jsonObject.getString("action");
                if(action.equals("on")) {
                    on();
                } else {
                    off();
                }
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

        Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("status", status);
                    String json = jsonObject.toString();
                    try {
                        mqttClient.publish(pubTopic, json.getBytes(), 0, false);
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

}
