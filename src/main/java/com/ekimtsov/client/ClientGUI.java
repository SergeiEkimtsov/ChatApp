package com.ekimtsov.client;

import com.ekimtsov.LoggerClass;
import com.ekimtsov.server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ClientGUI extends JFrame {
    private String nameChat;
    private JTextArea messageArea;
    private  JTextField textField;
    private Client client;
    private JButton exitButton;

    public ClientGUI(Client client) throws IOException {
            this.client = client;
            nameChat = JOptionPane.showInputDialog(this
                    , "Enter your name:"
                    , null, JOptionPane.PLAIN_MESSAGE);

            //verifying name in collection mapSocketListener from Server
            while (Server.getMapSocketListener().containsValue(nameChat) || nameChat.equals("")) {
                nameChat = JOptionPane.showInputDialog(this
                        , "Your entered name is existed or name is empty. Enter name again:"
                        , null, JOptionPane.PLAIN_MESSAGE);
            }

            setTitle("Chat Application " + "user: " + nameChat);
            setSize(400, 400);
            setDefaultCloseOperation(EXIT_ON_CLOSE);

            Color backgroundColor = new Color(0xFFFFFF);
            Color buttonColor = new Color(0xABABAB);
            Color textColor = new Color(50, 50, 50);
            Font buttonFont = new Font("Arial", Font.BOLD, 12);
            Font textFont = new Font("Arial", Font.BOLD, 14);

            messageArea = new JTextArea();
            messageArea.setBackground(backgroundColor);
            messageArea.setEditable(false);
            messageArea.setFont(textFont);
            add(new JScrollPane(messageArea), BorderLayout.CENTER);

            textField = new JTextField();
            textField.setForeground(textColor);
            textField.setFont(textFont);
            textField.setBackground(backgroundColor);
            textField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String message = "["
                            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss"))
                            + "] "
                            + nameChat + ": "
                            + textField.getText();
                    client.sendMessage(message);
                    textField.setText("");
                }
            });

            exitButton = new JButton("Exit");
            exitButton.setFont(buttonFont);
            exitButton.setBackground(buttonColor);
            exitButton.setForeground(Color.WHITE);

            exitButton.addActionListener(s -> {
                client.sendMessage("exit");
                setVisible(false);
            });

            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.add(textField, BorderLayout.CENTER);
            bottomPanel.add(exitButton, BorderLayout.EAST);
            bottomPanel.setBackground(backgroundColor);

            add(bottomPanel, BorderLayout.SOUTH);

            this.setVisible(true);
    }
    public String getNameChat() {
        return nameChat;
    }
    public void showMessageFromServer(String message){
        messageArea.append(message+"\n");
    }
}
