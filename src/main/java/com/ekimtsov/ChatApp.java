package com.ekimtsov;

import com.ekimtsov.client.Client;
import com.ekimtsov.server.Server;
import java.io.IOException;

/**
 * Start Server!
 */
public class ChatApp {
    public static void main(String[] args) throws IOException {
        new Server();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        new Client("localhost", 1500);


    }
}
