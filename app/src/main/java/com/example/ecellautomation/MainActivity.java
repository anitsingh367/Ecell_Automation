package com.example.ecellautomation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {


    Switch simpleSwitch1;
    static String MQTTHOST = "tcp://broker.hivemq.com:1883";
    String lightCommandTopic = "kcecell/esp/command";
    String lightStatusTopic = "kcecell/esp/status";
    MqttAndroidClient client;
    boolean LEDState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simpleSwitch1 = (Switch) findViewById(R.id.switch1);

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);

        MqttConnectOptions options = new MqttConnectOptions();

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(MainActivity.this, "Connected!!", Toast.LENGTH_SHORT).show();
                    setSub();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(MainActivity.this, "FAILED!!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        // This is a callback for subscribe i.e loop for incoming messages.
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, final MqttMessage message) throws Exception {

                if ((char) message.getPayload()[0] == '1') {
                    if (!LEDState) {
                        LEDState = true;
                        simpleSwitch1.setChecked(true);
                    }
                }


                // The toggle is disabled
                if ((char) message.getPayload()[0] == '0') {
                    if (LEDState) {
                        LEDState = false;
                        simpleSwitch1.setChecked(false);
                    }
                }
            }

//                });
//                Toast.makeText(MainActivity.this, new String(message.getPayload()), Toast.LENGTH_SHORT).show();
//                if ((char) message.getPayload()[0] == '1') {
//                    if (LEDState == false) {
//                        LEDState = true;
//                        simpleSwitch1.setChecked(true);
//                    }
//                }
//                if ((char) message.getPayload()[0] == '0') {
//                    if (LEDState == true) {
//                        LEDState = false;
//                        simpleSwitch1.setChecked(false);
//                    }
//                }
//
//
//                //client.unsubscribe(lightStatusTopic);
//            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        simpleSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!LEDState) {        // If Led is off and toggle is set to true
                        pub("1");
                        LEDState = true;
                        Toast.makeText(MainActivity.this, "ON!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (LEDState) {         // If Led is on and toggle is set to false
                        // The toggle is disabled
                        pub("0");
                        LEDState = false;
                        Toast.makeText(MainActivity.this, "OFF!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    // Function to Publish to a topic.
    public void pub(String message) {

        try {
            client.publish(lightCommandTopic, message.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    // Function to Subscribe to a topic.
    private void setSub() {
        try {
            client.subscribe(lightStatusTopic, 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}

