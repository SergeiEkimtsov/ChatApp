package com.ekimtsov.server;


import com.ekimtsov.LoggerClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;


public class ServerGUI extends JFrame {

    private  DefaultListModel<String> model = new DefaultListModel<>();
    JList<String> listMessage = new JList<>();
    private JTextArea clientData = new JTextArea();
    private  static int count=0;

    private StringBuilder listOfUsers = new StringBuilder();

    private final Font TEXT_FONT= new Font("SanSerif", Font.BOLD,14);

    public ServerGUI() throws HeadlessException {
        init();
        setTitle("Server "+ "Date: "+LocalDate.now());
        setSize(600,300);
        setVisible(true);
    }

    private void init() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setOneTouchExpandable(true);

        listMessage.setModel(model);
        listMessage.setFont(TEXT_FONT);
        listMessage.setBackground(new Color(0xFFFFFF));

        clientData.setEditable(false);
        clientData.setBackground(new Color(0xFFFFFF));
        clientData.setFont(TEXT_FONT);

        JScrollPane jScrollPaneLeft = new JScrollPane(listMessage);
        JScrollPane jScrollPaneRight= new JScrollPane(clientData);

        splitPane.setLeftComponent(jScrollPaneLeft);
        splitPane.setRightComponent(jScrollPaneRight);

        getContentPane().add(splitPane,BorderLayout.CENTER);

        JPanel panel = new JPanel();
        JButton saveButton = new JButton("save");

        panel.add(saveButton);
        add(panel,BorderLayout.SOUTH);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAllMessages(model, listOfUsers);
            }
        });

        //server can select client by double click and send message
        listMessage.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==2) {
                    StringBuilder sb = new StringBuilder(model.get(listMessage.getSelectedIndex()));
                    var name = sb.substring(11, sb.lastIndexOf(":"));
                    var result = JOptionPane.showInputDialog(null, "Reply to " + name);
                    var mapListeners = Server.getMapSocketListener();

                    for (Map.Entry<SocketListener, String> entry : mapListeners.entrySet()) {
                        if (entry.getValue().equals(name))
                            entry.getKey().sendToClient(result);
                    }
                }
            }
        });
    }

    public  synchronized void addMessage(String message){
        model.add(count,message);
        count++;
       // System.out.println("count "+count);
    }
    public  synchronized void addUser(String message){
        listOfUsers.append(message).append("\n");
        clientData.setText(listOfUsers.toString());

    }
    public void saveAllMessages(DefaultListModel<String> listMessages, StringBuilder listOfUsers){
        LoggerClass.logger.info("Save all messages and list of users");
        StringBuilder messages = new StringBuilder();
        for (int i=0; i<listMessages.size();i++){
            messages.append(listMessages.get(i)).append("\n");
        }
        try (FileWriter writer1 = new FileWriter(new File("messages.txt"));
                FileWriter writer2 = new FileWriter(new File("users.txt"))){
            writer1.write(String.valueOf(messages));
            writer2.write(String.valueOf(listOfUsers));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new ServerGUI());
//    }
}
