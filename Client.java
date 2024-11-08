import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Client {
    private static JTextArea chatArea;
    private static JTextField messageField;
    private static DataOutputStream dataOutputStream;

    public static void main(String[] args) {
        // Setup GUI
        JFrame frame = new JFrame("Client Chat");
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBackground(new Color(30, 30, 30));
        chatArea.setForeground(Color.WHITE);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(chatArea);

        messageField = new JTextField(30);
        messageField.setFont(new Font("Arial", Font.PLAIN, 16));
        messageField.setPreferredSize(new Dimension(300, 40));
        messageField.setBackground(new Color(50, 50, 50));
        messageField.setForeground(Color.WHITE);
        messageField.setBorder(new EmptyBorder(0, 10, 0, 0)); // Adds left padding for better cursor visibility

        JButton sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(80, 40));
        sendButton.setBackground(new Color(70, 130, 180));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);

        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        try {
            Socket socket = new Socket("localhost", 1234);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            sendButton.addActionListener((ActionEvent e) -> sendMessage());
            messageField.addActionListener((ActionEvent e) -> sendMessage());

            Thread receiveThread = new Thread(() -> {
                try {
                    String message;
                    while (true) {
                        message = dataInputStream.readUTF();
                        chatArea.append("Server: " + message + "\n");
                        if ("exit".equalsIgnoreCase(message)) {
                            chatArea.append("Server has ended the chat.\n");
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            receiveThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            try {
                chatArea.append("Me: " + message + "\n");
                dataOutputStream.writeUTF(message);
                dataOutputStream.flush();

                if ("exit".equalsIgnoreCase(message)) {
                    messageField.setEnabled(false);
                }
                messageField.setText("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}