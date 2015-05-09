package com.github.matobet.doctor.push;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.List;

public class PushServiceImpl implements PushService, MqttCallback {

    public PushServiceImpl() {

        initMqttClient();
        connect();
    }

    private static final String BROKER = "tcp://localhost:1883";
    private static final String CLIENT_ID = "Doctor Rest";
    private static final String USER_NAME = "doctor";
    private static final String PASSWORD = "gallifrey";

    private static final int QOS_GUARANTEE = 2;

    private MqttAsyncClient client;
    private boolean connected;

    private void connect() {
        try {
            client.connect(getConnectionOptions(), null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    connected = true;
                    System.out.println("Connected MQTT PushService");
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void initMqttClient() {
        try {
            client = new MqttAsyncClient(BROKER, CLIENT_ID, new MemoryPersistence());
            client.setCallback(this);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private MqttConnectOptions getConnectionOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USER_NAME);
        options.setPassword(PASSWORD.toCharArray());
        return options;
    }

    @Override
    public void pushCreate(String name, String id) {
        try {
            client.publish(getTopic(name, id), getTextMessage("+"));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pushUpdate(String name, String id, List<String> fields) {
        try {
            client.publish(getTopic(name, id), getTextMessage(StringUtils.join(fields)));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pushDelete(String name, String id) {
        try {
            client.publish(getTopic(name, id), getTextMessage("-"));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    private String getTopic(String name, String id) {
        return name + "/" + id;
    }

    private MqttMessage getTextMessage(String text) {
        MqttMessage message = new MqttMessage(text.getBytes());
        message.setQos(QOS_GUARANTEE);
        return message;
    }
}
