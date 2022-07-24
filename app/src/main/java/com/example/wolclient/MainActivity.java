package com.example.wolclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    WolClient wolClient = new WolClient();
    Boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button status = findViewById(R.id.status);
        Button start = findViewById(R.id.start);
        Button stop = findViewById(R.id.stop);
        Button makeConnection = findViewById(R.id.make_connection);
        Button closeConnection = findViewById(R.id.close_connection);


        makeConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConnectionWithMessageEncapsulation(Config.host, Config.port);
            }
        });

        closeConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeConnectionWithMessageEncapsulation();
            }
        });


        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected) {
                    String message;
                    message = getServerStatus();
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "Mai intai realizeaza o conexiune", Toast.LENGTH_SHORT).show();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected) {

                    String message = startServer();

                    if (message != null)
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "Mai intai realizeaza o conexiune", Toast.LENGTH_SHORT).show();
            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected) {
                    String message = stopServer();

                    if (message != null)
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "Mai intai realizeaza o conexiune", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onPause(){
        closeConnectionWithMessageEncapsulation();
        super.onPause();
    }

    public void onResume(){
        startConnectionWithMessageEncapsulation(Config.host, Config.port);
        super.onResume();
    }


    private String startConnection(String host, Integer port){
        NetworkWorker networkWorker = new NetworkWorker() {
            @Override
            public void run() {
                try {
                    wolClient.startConnection(host, port);
                    message = "Conexiune pornita cu succes";
                    isConnected = true;
                }
                catch (IOException e){
                    message = "Nu am reusit sa fac conexiunea";
                }
            }
        };

        startSeparateThreadToDoDirtyWorkAndWait(networkWorker);
        return networkWorker.getMessage();
    }

    private void startConnectionWithMessageEncapsulation(String host, Integer port) {
        if (!isConnected){
            String message;
            message = startConnection(host, port);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            if (!message.equals("Conexiune pornita cu succes")){
                Toast.makeText(getApplicationContext(), "Verifica conexiunea la internet si incearca din nou sau verifica serverul", Toast.LENGTH_SHORT).show();
            }
        }
        else
            Toast.makeText(getApplicationContext(), "Deja conectat", Toast.LENGTH_SHORT).show();
    }

    private String closeConnection() {
        NetworkWorker networkWorker = new NetworkWorker() {
            @Override
            public void run() {
                try {
                    message = wolClient.closeConnection();
                    isConnected = false;
                }
                catch (IOException e){
                    message = "Nu am reusit sa inchid conexiunea";
                }
            }
        };

        startSeparateThreadToDoDirtyWorkAndWait(networkWorker);
        return networkWorker.getMessage();
    }

    private void closeConnectionWithMessageEncapsulation(){
        if (isConnected) {
            String message;
            message = closeConnection();
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(), "Nu exista conexiune de inchis", Toast.LENGTH_SHORT).show();
    }


    private String getServerStatus(){

        NetworkWorker networkWorker = new NetworkWorker() {
            @Override
            public void run() {
                try {
                    message = wolClient.getServerStatus();
                }
                catch (IOException e){
                    message = "Nu am reusit sa fac conexiunea";
                }
            }
        };

        startSeparateThreadToDoDirtyWorkAndWait(networkWorker);
        return networkWorker.getMessage();
    }

    private String startServer(){
        NetworkWorker networkWorker = new NetworkWorker() {
            @Override
            public void run() {
                try {
                    message = wolClient.startServer();
                }
                catch (IOException e){
                    message = "Nu am reusit sa comunic pornirea serverului";
                }
            }
        };

        startSeparateThreadToDoDirtyWorkAndWait(networkWorker);
        return networkWorker.getMessage();
    }

    private String stopServer(){
        NetworkWorker networkWorker = new NetworkWorker() {
            @Override
            public void run() {
                try {
                    message = wolClient.stopServer();
                }
                catch (IOException e){
                    message = "Nu am reusit sa comunic oprirea serverului";
                }
            }
        };

        startSeparateThreadToDoDirtyWorkAndWait(networkWorker);
        return networkWorker.getMessage();
    }

    private void startSeparateThreadToDoDirtyWorkAndWait(NetworkWorker networkWorker) {
        Thread thread = new Thread(networkWorker);
        thread.start();

        try {
            thread.join();
        }
        catch (InterruptedException e){
            Toast.makeText(getApplicationContext(), "Thread intrerupt din motive necunoscute!", Toast.LENGTH_SHORT).show();
        }
    }

}