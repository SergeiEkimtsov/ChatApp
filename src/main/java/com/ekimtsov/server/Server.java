package com.ekimtsov.server;

import com.ekimtsov.LoggerClass;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable{
    private static final List<SocketListener> listSocketListener = new ArrayList<>();
    private static final Map<SocketListener,String> mapSocketListener = new HashMap<>();



    public Server() {
        new Thread(this).start();
    }

    @Override
    public void run() {

        ServerGUI serverGUI = new ServerGUI();


        LoggerClass.logger.info("Server started");

        ServerSocket serverSocket;
        try {
            int PORT = 1500;
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            try {
                var socket = serverSocket.accept();
                SocketListener socketListener = new SocketListener(socket, serverGUI, this);
                listSocketListener.add(socketListener);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                mapSocketListener.put(socketListener,socketListener.nameClient);
                for (Map.Entry<SocketListener,String> entry:mapSocketListener.entrySet()) {
                   LoggerClass.logger.info("socketListener: "+entry.getKey()+" - nameChat: "+entry.getValue());
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<SocketListener> getListSocket() {
        return listSocketListener;
    }

    public static Map<SocketListener, String> getMapSocketListener() {
        return mapSocketListener;
    }

}
