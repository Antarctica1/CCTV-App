package com.example.raspberrypi_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public static String ip;
    private static int port = 8080;
    private Socket client;
    private DataOutputStream dataOutput;
    private DataInputStream dataInput;
    private static String CONNECT_MSG = "Connect";
    private static String STOP_MSG = "stop";


    EditText Edit;
    Button connect;
    TextView Check, temphumid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Edit = (EditText) findViewById(R.id.EdtText);
        connect = (Button) findViewById(R.id.Connection);
        Check = (TextView) findViewById(R.id.tempTextview);
        temphumid = (TextView) findViewById(R.id.humidity);

        connect.setOnClickListener(new View.OnClickListener() { //연결 버튼 눌렀을 때 발생하는 이벤트
            @Override
            public void onClick(View view) {
                ip = Edit.getText().toString();
                Connect connect = new Connect();
                connect.execute(CONNECT_MSG);
            }
        });


    }



    public void btnClick(View v){ // MainActivity에서 cctvActivity로 화면 전환
        Intent intent = new Intent(MainActivity.this, cctvActivity.class);
        intent.putExtra("ip", ip);
        startActivity(intent);
        finish();
    }

    private class Connect extends AsyncTask< String , String,Void > {
        private String output_message;
        private String input_message;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                client = new Socket(ip, port);
                dataOutput = new DataOutputStream(client.getOutputStream());
                dataInput = new DataInputStream(client.getInputStream());
                output_message = strings[0];
                dataOutput.writeUTF(output_message);

            } catch (UnknownHostException e) {
                String str = e.getMessage().toString();
                Log.w("discnt", str + " 1");
            } catch (IOException e) {
                String str = e.getMessage().toString();
                Log.w("discnt", str + " 2");
            }

            while (true){
                try {
                    byte[] buf = new byte[100];
                    int read_Byte  = dataInput.read(buf);
                    input_message = new String(buf, 0, read_Byte);
                    if (!input_message.equals(STOP_MSG)){
                        publishProgress(input_message);
                    }
                    else{
                        break;
                    }
                    Thread.sleep(2);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... params){
            Check.setText(""); // Clear the chat box
            Check.append(output_message);
            temphumid.setText(""); // Clear the chat box
            temphumid.append( params[0]);
        }
    }


}