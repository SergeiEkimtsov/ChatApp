package com.ekimtsov.server;

import com.ekimtsov.LoggerClass;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SocketListener implements Runnable {

    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final ServerGUI serverGUI;
    private final Server server;
    String nameClient;

    public SocketListener(Socket socket,  ServerGUI serverGUI, Server server) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.serverGUI = serverGUI;
        this.server = server;
        new Thread(this).start();
    }
    @Override
    public void run() {
        String wordFromClient;
        try {
            wordFromClient =in.readLine(); //first message is name of client
            nameClient = wordFromClient;
            serverGUI.addUser("user " + nameClient+" jointed to chat");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true){
            try {
                wordFromClient =in.readLine();
                if (wordFromClient.equals("exit")){
                    serverGUI.addUser("user " + nameClient + " left chat");
                    server.getListSocket().remove(this); //delete from list of socketListener
                    break;
                }
                else {
                    serverGUI.addMessage(wordFromClient);

                    for (SocketListener socketListener:server.getListSocket()){    //sent message to all clients
                            socketListener.out.write(wordFromClient + "\n");
                            socketListener.out.flush();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        closeSocket();
    }

    public void sendToClient(String message){
        try {
            this.out.write("["
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss"))
                    + "] "
                    +"Server: "
                    + message+"\n");
            this.out.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
}
    public void closeSocket() {
        try {
            LoggerClass.logger.info("socket started closing");
            socket.close();
            in.close();
            out.close();
            LoggerClass.logger.info("socket is closed");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
