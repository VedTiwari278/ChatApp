import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
public class Recive {
  private static JTextArea chatArea;
  private static JTextField messageField;
  private static DataOutputStream dataOutputStream;

  public static void main(String[] args) {
      JFrame frame = new JFrame("Server Chat");
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

      try (ServerSocket serverSocket = new ServerSocket(1234)) {
          chatArea.append("Server is listening on port 1234...\n");
          Socket socket = serverSocket.accept();
          chatArea.append("Client connected.\n");

          dataOutputStream = new DataOutputStream(socket.getOutputStream());
          DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

          sendButton.addActionListener((ActionEvent e) -> sendMessage());
          messageField.addActionListener((ActionEvent e) -> sendMessage());

          Thread receiveThread = new Thread(() -> {
              try {
                  String message;
                  while (true) {
                      message = dataInputStream.readUTF();
                      chatArea.append("Client: " + message + "\n");
                      if ("exit".equalsIgnoreCase(message)) {
                          chatArea.append("Client has ended the chat.\n");
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