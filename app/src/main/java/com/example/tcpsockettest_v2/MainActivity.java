package com.example.tcpsockettest_v2;

import static android.content.ContentValues.TAG;

import androidx.annotation.AnyThread;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private Socket client;
    private PrintWriter printwriter;
    private EditText editText;
    private TextView textView;
    private Button button,connect;
    private Thread thread;
    private ClientThread clientThread;
    private String message;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // reference to the text field
        editText = findViewById(R.id.editText1);
        textView = findViewById(R.id.textView);

        // reference to the send button
        button = findViewById(R.id.button1);
        connect = findViewById(R.id.connect);

        //initiate handler
        handler = new Handler();

        // Button press event listener to connect
        connect.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // start the Thread to connect to server
                invokeServer();
            }
        });

        // invoke clientthread send message service when clicked
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // get the text message on the text field
                message = editText.getText().toString();

                // start the Thread to connect to server
                Log.d(TAG, "onClick: reached here6");

                send(message);
            }
        });
        //new Thread(new ReceiverThread()).start();
    }

    private void invokeServer(){
        Log.d(TAG, "onClick: reached here1");
        thread = new Thread(clientThread = new ClientThread());
        Log.d(TAG, "onClick: reached here2");
        thread.start();
        Log.d(TAG, "onClick: reached here3");
    }

    private void send(String message){
        clientThread.sendMessage(message);
    }


    class ClientThread extends Thread {

        private InetAddress serverAddr;
        private BufferedReader input;

        @Override
        public void run() {
                // the IP and port should be correct to have a connection established
                // Creates a stream socket and connects it to the specified port number on the named host.

            try {
                Log.d(TAG, "onClick: reached here4");
                serverAddr = InetAddress.getByName("192.168.1.39");
                Log.d(TAG, "onClick: reached here5");
                client = new Socket(serverAddr, 4444); // connect to server
                Log.d(TAG, "onClick: Server connection created");
                //boolean flag = false;
                while (true){
                    Log.d(TAG, "onClick: reached here6");
                    this.input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String mess = this.input.readLine();
                    Log.d(TAG, "run: hh");
                    if(mess != "Over"){
                        showMessage(mess);
                    }else{
                        break;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(final String message){
            try {
                printwriter = new PrintWriter(client.getOutputStream(),true);
                printwriter.write(message);
                printwriter.flush();
                printwriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void showMessage(final String message){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    textView.setText(message);
                }
            });
        }

    }

//    class ReceiverThread implements Runnable{
//
//        private BufferedReader input;
//
//        @Override
//        public void run() {
//
//            try {
//                //input = new InputStreamReader(client.getInputStream(), String.valueOf(true));
//                InetAddress serverAdd = InetAddress.getByName("192.168.1.39");
//                client = new Socket(serverAdd,4444);
//                while (!Thread.currentThread().isInterrupted()){
//                    this.input = new BufferedReader(new InputStreamReader(client.getInputStream()));
//                    String mess = this.input.readLine();
//                    Log.d(TAG, "run: ");
//                    showMessage(mess);
//                }
//
//                input.close();
//                client.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void showMessage(final String message){
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                textView.setText(message);
//            }
//        });
//    }
}