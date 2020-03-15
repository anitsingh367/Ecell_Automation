package com.example.ecellautomation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {


    Switch simpleSwitch1;
    Switch simpleSwitch2;
    Switch simpleSwitch3;
    Switch simpleSwitch4;

    MqttAndroidClient client;
    static String MQTTHOST = "tcp://broker.hivemq.com:1883";
    String commandTopic = "kcecell/esp/command";
    String statusTopic = "kcecell/esp/status";


    boolean light1State = false;
    boolean light2State = false;
    boolean fan1State = false;
    boolean fan2State = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simpleSwitch1 = (Switch) findViewById(R.id.switch1); // Declaring the 1st switch
        simpleSwitch2 = (Switch) findViewById(R.id.switch2); // Declaring the 2nd switch
        simpleSwitch3 = (Switch) findViewById(R.id.switch3); // Declaring the 3rd switch
        simpleSwitch4 = (Switch) findViewById(R.id.switch4); // Declaring the 4th switch


        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);
        MqttConnectOptions options = new MqttConnectOptions();

//Connect to the MQTT broker

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

/*
Declaring the Switch event Listeners
* 1. Light 1
* 2. Light 2
* 3. Fan 1
* 4. Fan 2
*/

        simpleSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!light1State) {        // If Led is off and toggle is set to true
                        pub("light1", "1");           // Change this func. to a func. that accepts state as para and converts it into JSON and then publishes acc.
                        light1State = true;
                        Toast.makeText(MainActivity.this, "light1 ON!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (light1State) {         // If Led is on and toggle is set to false
                        pub("light1", "0");           // Change this func. to a func. that accepts state as para and converts it into JSON and then publishes acc.
                        light1State = false;
                        Toast.makeText(MainActivity.this, "light1 OFF!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        simpleSwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!light2State) {        // If Led is off and toggle is set to true
                        pub("light2", "1");           // Change this func. to a func. that accepts state as para and converts it into JSON and then publishes acc.
                        light2State = true;
                        Toast.makeText(MainActivity.this, "light2 ON!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (light2State) {         // If Led is on and toggle is set to false
                        pub("light2", "0");           // Change this func. to a func. that accepts state as para and converts it into JSON and then publishes acc.
                        light2State = false;
                        Toast.makeText(MainActivity.this, "light2 OFF!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        simpleSwitch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!fan1State) {        // If Led is off and toggle is set to true
                        pub("fan1", "1");           // Change this func. to a func. that accepts state as para and converts it into JSON and then publishes acc.
                        fan1State = true;
                        Toast.makeText(MainActivity.this, "FAN1 ON!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (fan1State) {         // If Led is on and toggle is set to false
                        pub("fan1", "0");           // Change this func. to a func. that accepts state as para and converts it into JSON and then publishes acc.
                        fan1State = false;
                        Toast.makeText(MainActivity.this, "FAN1 OFF!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        simpleSwitch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!fan2State) {        // If Led is off and toggle is set to true
                        pub("fan2", "1");           // Change this func. to a func. that accepts state as para and converts it into JSON and then publishes acc.
                        fan2State = true;
                        Toast.makeText(MainActivity.this, "FAN2 ON!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (fan2State) {         // If Led is on and toggle is set to false
                        pub("fan2", "0");           // Change this func. to a func. that accepts state as para and converts it into JSON and then publishes acc.
                        fan2State = false;
                        Toast.makeText(MainActivity.this, "FAN2 OFF!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


// This is a callback for subscribe i.e loop for incoming messages.
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, final MqttMessage message) throws Exception {


/*if ((char) message.getPayload()[0] == '1') {
if (!light1State) {
light1State = true;
simpleSwitch1.setChecked(true);
}
}

if ((char) message.getPayload()[0] == '0') {
if (light1State) {
light1State = false;
simpleSwitch1.setChecked(false);
}
}*/
                Toast.makeText(MainActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
                JSONObject espStatus = new JSONObject(message.toString());
                String applianceName = espStatus.getString("appliance");
                String state = espStatus.getString("state");


                switch (applianceName) {
                    case "light1":
                        if (state.charAt(0) == '1') {
                            if (!light1State) {
                                light1State = true;
                                simpleSwitch1.setChecked(true);
                            }
                        }

                        if (state.charAt(0) == '0') {
                            if (light1State) {
                                light1State = false;
                                simpleSwitch1.setChecked(false);
                            }
                        }
                        break;

                    case "light2":
                        if (state.charAt(0) == '1') {
                            if (!light2State) {
                                light2State = true;
                                simpleSwitch2.setChecked(true);
                            }
                        }

                        if (state.charAt(0) == '0') {
                            if (light2State) {
                                light2State = false;
                                simpleSwitch2.setChecked(false);
                            }
                        }
                        break;

                    case "fan1":
                        if (state.charAt(0) == '1') {
                            if (!fan1State) {
                                fan1State = true;
                                simpleSwitch3.setChecked(true);
                            }
                        }

                        if (state.charAt(0) == '0') {
                            if (fan1State) {
                                fan1State = false;
                                simpleSwitch3.setChecked(false);
                            }
                        }
                        break;
                    case "fan2":
                        if (state.charAt(0) == '1') {
                            if (!fan2State) {
                                fan2State = true;
                                simpleSwitch4.setChecked(true);
                            }
                        }

                        if (state.charAt(0) == '0') {
                            if (fan2State) {
                                fan2State = false;
                                simpleSwitch4.setChecked(false);
                            }
                        }
                        break;
                    default:
                        Toast.makeText(MainActivity.this, "No Matching Case", Toast.LENGTH_SHORT).show();
                }

            }   // Change this to cases


            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });


    }

    // Function to Publish to a topic.
    public void pub(String appliance, String message) {
        JSONObject publishMsg = new JSONObject();
        try {
            publishMsg.put("appliance", appliance);
            publishMsg.put("state", message);
            client.publish(commandTopic, publishMsg.toString().getBytes(), 0, false);

        } catch (MqttException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // Function to Subscribe to a topic.
    private void setSub() {
        try {
            client.subscribe(statusTopic, 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}

