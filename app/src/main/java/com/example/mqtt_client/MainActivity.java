package com.example.mqtt_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

   private static final String TAG = "MainActivity";
   private MqttAndroidClient client;
   private  Integer Qos = 0;
   private  String  host, port, topic, clientID, mes;
   private Button btnConnect, btnClientID, btnPublish, btnSubscriptions;
   private CardView cvConnect, cvPublish, cvSubscriptions;
   private EditText edHost,edPort,edClientID, edTopicP, edTopicS, edQosP, edQosS, edMessagesP;
   private ListView listMessages;
   private ArrayAdapter<String> adapter;
   private ArrayList<String> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cvConnect = findViewById(R.id.cvConnect);
        cvPublish = findViewById(R.id.cvPublish);
        cvSubscriptions = findViewById(R.id.cvSubscriptions);

        edHost = findViewById(R.id.editTextHost);
        edPort = findViewById(R.id.editTextPort);
        edTopicP= findViewById(R.id.edTopic);
        edTopicS= findViewById(R.id.edTopicSub);
        edQosP= findViewById(R.id.edQoS);
        edQosS= findViewById(R.id.edQoSSub);
        edMessagesP= findViewById(R.id.edMessages);

        listMessages = findViewById(R.id.listMes);

        btnPublish = findViewById(R.id.btnPublish);
        btnSubscriptions = findViewById(R.id.btnSub);

        edClientID = findViewById(R.id.editTextClientID);
        btnConnect = findViewById(R.id.buttonConnect);
        btnClientID = findViewById(R.id.btnGeneratClientID);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, messages);




        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });

        btnClientID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edClientID.setText(MqttClient.generateClientId().toString());
            }
        });

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pub();
            }
        });

        btnSubscriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sub();
            }
        });

    }
    
    private  void connect()
    {
        if(!TextUtils.isEmpty(edClientID.getText().toString()))
        {
            clientID = edClientID.getText().toString();
        }
        else
        {
            Toast toast =  Toast.makeText(MainActivity.this,
                    "Введите ID клиента!",Toast.LENGTH_SHORT);
            toast.show();
        }

        if(!TextUtils.isEmpty(edHost.getText().toString()))
        {
            host = edHost.getText().toString();
        }
        else
        {
            Toast toast =  Toast.makeText(MainActivity.this,
                    "Введите адрес хоста!",Toast.LENGTH_SHORT);
            toast.show();
        }

        if(!TextUtils.isEmpty(edPort.getText().toString()))
        {
            port = edPort.getText().toString();
        }
        else
        {
            Toast toast =  Toast.makeText(MainActivity.this,
                    "Введите номер порта!",Toast.LENGTH_SHORT);
            toast.show();
        }


        client =
                new MqttAndroidClient(MainActivity.this, "tcp://" + host + ":"+ port,
                        clientID);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "onSuccess");
                    cvConnect.setVisibility(View.GONE);
                    cvPublish.setVisibility(View.GONE);
                    cvSubscriptions.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    private  void pub()
    {
        if(!TextUtils.isEmpty(edTopicP.getText().toString()))
        {
            topic = edTopicP.getText().toString();
        }
        else
        {
            Toast toast =  Toast.makeText(MainActivity.this,
                    "Введите топик",Toast.LENGTH_SHORT);
            toast.show();
        }

        if(!TextUtils.isEmpty(edMessagesP.getText().toString()))
        {
            mes = edMessagesP.getText().toString();
        }
        else
        {
            Toast toast =  Toast.makeText(MainActivity.this,
                    "Введите сообщение!",Toast.LENGTH_SHORT);
            toast.show();
        }

        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = mes.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    private  void sub()
    {
        if(!TextUtils.isEmpty(edQosS.getText().toString()))
        {
            Qos = Integer.parseInt(edQosS.getText().toString());
        }
        else
        {
            Toast toast =  Toast.makeText(MainActivity.this,
                    "Введите значение Qos",Toast.LENGTH_SHORT);
            toast.show();
        }

        if(!TextUtils.isEmpty(edTopicS.getText().toString()))
        {
            topic = edTopicS.getText().toString();
        }
        else
        {
            Toast toast =  Toast.makeText(MainActivity.this,
                    "Введите топик",Toast.LENGTH_SHORT);
            toast.show();
        }

        try {
            client.subscribe(topic, Qos);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) { //Called when the client lost the connection to the broker
                }
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d(TAG,"messages: " + new String(message.getPayload()));
                    messages.add("topic: " + topic + "\n" + "messages: "+  new String(message.getPayload()));
                    Log.d(TAG,"topic: " + topic);
                    listMessages.setAdapter(adapter);
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {//Called when a outgoing publish is complete
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_publish :
                cvConnect.setVisibility(View.GONE);
                cvPublish.setVisibility(View.VISIBLE);
                cvSubscriptions.setVisibility(View.GONE);
                return true;
            case R.id.action_sub:
                cvConnect.setVisibility(View.GONE);
                cvPublish.setVisibility(View.GONE);
                cvSubscriptions.setVisibility(View.VISIBLE);
                return true;
            case R.id.action_connect:
                cvConnect.setVisibility(View.VISIBLE);
                cvPublish.setVisibility(View.GONE);
                cvSubscriptions.setVisibility(View.GONE);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}