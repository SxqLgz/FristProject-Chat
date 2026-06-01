package com.idea.UI;

import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatEntryFrame extends JFrame {

    private JTextField IP;
    private JTextField nicknameField;
    private JButton enterButton;
    private JButton cancleButton;
    private Socket socket;//和服务端建立连接的管道

    public ChatEntryFrame() {
        setTitle("局域网聊天");
        setSize(400,150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);//禁止调整大小

        getContentPane().setBackground(Color.decode("#F0F0F0"));
//          主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.decode("#F0F0F0"));
        add(mainPanel);

//        顶部面板
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,10));
        topPanel.setBackground(Color.decode("#F0F0F0"));
//         IP输入框
        JLabel ipLable = new JLabel("服务器IP");
        ipLable.setFont(new Font("楷体",Font.PLAIN,16));
        IP = new JTextField("192.168.0.108",14);
        IP.setFont(new Font("楷体",Font.PLAIN,16));
        topPanel.add(ipLable);
        topPanel.add(IP);

//        标签和文本框
        JLabel nicknameLabel = new JLabel("昵称");
        nicknameLabel.setFont(new Font("楷体",Font.PLAIN,16));
        nicknameField = new JTextField(10);
        nicknameField.setFont(new Font("楷体",Font.PLAIN,16));
        nicknameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,1,1,1,Color.GRAY),
                BorderFactory.createEmptyBorder(5,5,5,5)
        ));

        topPanel.add(nicknameLabel);
        topPanel.add(nicknameField);
        mainPanel.add(topPanel, BorderLayout.NORTH);

//        按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,10));
        buttonPanel.setBackground(Color.decode("#F0F0F0"));

        enterButton = new JButton("登录");
        enterButton.setFont(new Font("楷体",Font.PLAIN,16));
        enterButton.setBackground(Color.decode("#007BFF"));
        enterButton.setForeground(Color.WHITE);
        enterButton.setBorderPainted(false);
        enterButton.setFocusPainted(false);

        cancleButton = new JButton("取消");
        cancleButton.setFont(new Font("楷体",Font.PLAIN,16));
        cancleButton.setBackground(Color.decode("#CD3545"));
        cancleButton.setForeground(Color.WHITE);
        cancleButton.setBorderPainted(false);
        cancleButton.setFocusPainted(false);

        buttonPanel.add(enterButton);
        buttonPanel.add(cancleButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

//        添加监听器
        enterButton.addActionListener(e-> {
            String nickname = nicknameField.getText();
            nicknameField.setText("");
            //判断昵称是否为空
            if (!nickname.isEmpty()) {
                try {
                    //1、登录
                    login(nickname);
                    //登录成功候进入聊天逻辑
                    new ClientChatFrame(socket,nickname);
                    //关闭登录窗口
                    dispose();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }else {
                JOptionPane.showMessageDialog(this,"请输入昵称");
            }
        });

        cancleButton.addActionListener(e-> System.exit(0));

        this.setVisible(true);
    }

//---------------------------------发送昵称方法---------------------------------------
    private void login(String nickname) throws IOException {
//        首先和服务端建立连接
        socket = new Socket(IP.getText(), Constant.SERVER_PORT);
//        将获取的昵称发送给服务端，发送1告诉服务端接下来发送的是管道昵称信息
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeInt(1);
        dos.writeUTF(nickname);
        dos.flush();
    }
}
