package com.example.wolclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class WolClient {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;


    public void startConnection(String host, Integer port) throws java.io.IOException {
        clientSocket = new Socket(host, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String startServer() throws java.io.IOException {
        return commandServer("start");
    }

    public String stopServer() throws java.io.IOException{
       return commandServer("stop");
    }

    public String getServerStatus() throws java.io.IOException{
        return commandServer("status");
    }

    public String closeConnection() throws java.io.IOException{
        String returnedMessage = commandServer("exit");
        closeCurrentConnection();
        return returnedMessage;
    }

    private String commandServer(String command) throws java.io.IOException{
        out.println(command);
        return in.readLine();
    }

    private void closeCurrentConnection() throws java.io.IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

}
