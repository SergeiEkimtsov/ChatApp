package com.ekimtsov.client;

import com.ekimtsov.LoggerClass;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable{

    private Socket socket;
    private BufferedReader reader;
    private BufferedReader in;
    private BufferedWriter out;

    private final ClientGUI clientGUI = new ClientGUI(this);

    public Client(String HOST, int PORT) throws IOException {
        this.socket = new Socket(HOST, PORT);
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        new Thread(this).start();
    }

    public void sendMessage(String message) {
        try {
            LoggerClass.logger.info(message);
            out.write(message + "\n");
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            this.sendMessage(clientGUI.getNameChat());//first message is the name chat

            String serverWord = in.readLine();
            while (serverWord!=null && !serverWord.equals("exit\n")) {
                clientGUI.showMessageFromServer(serverWord);
                serverWord = in.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                clientGUI.setVisible(false);
                socket.close();
                in.close();
                out.close();
                reader.close();
                LoggerClass.logger.info("User " + clientGUI.getNameChat()+" left chat");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
