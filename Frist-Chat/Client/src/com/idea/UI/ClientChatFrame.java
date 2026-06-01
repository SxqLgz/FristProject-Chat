package com.idea.UI;

import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.net.Socket;


public class ClientChatFrame extends JFrame {
    public JTextArea smsContent = new JTextArea(23,50);
    private JTextArea smsSend = new JTextArea(4,40);
    public JList<String> onLineUsers = new JList<>();
    private JButton sendBn = new JButton("发送");
    private Socket socket;

    public ClientChatFrame() {
        initView();
        this.setVisible(true);
    }

    public ClientChatFrame(Socket socket, String nickname) {
        this();//调用无参构造器启动窗口，初始化界面
        this.setTitle(nickname);//将昵称第一时间展示在聊天窗口的标题
        this.socket = socket;
//        登录成功后，立即接收到客户端的socket交给一个独立的线程来负责读取数据
        new ClientReaderThread(socket,this).start();
    }

    private void initView(){
        this.setSize(700,600);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

//        设置窗口颜色
        this.getContentPane().setBackground(new Color(0xf0, 0xf0, 0xf0));
//        设置字体
        Font font = new Font("SimKai",Font.PLAIN,14);
//        消息框
        smsContent.setFont(font);
        smsContent.setBackground(new Color(0xdd, 0xdd, 0xdd));
        smsContent.setEditable(false);
//        发送消息框
        smsSend.setFont(font);
        smsSend.setWrapStyleWord(true);
        smsSend.setLineWrap(true);
//        在线用户列表
        onLineUsers.setFont(font);
        onLineUsers.setFixedCellWidth(120);
        onLineUsers.setVisibleRowCount(13);
//        底部面板
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(0xf0, 0xf0, 0xf0));
//        消息输入框
        JScrollPane smsSendScrollPane = new JScrollPane(smsSend);
        smsSendScrollPane.setBorder(BorderFactory.createEmptyBorder());
        smsSendScrollPane.setPreferredSize(new Dimension(500,100));
//        发送按钮
        sendBn.setFont(font);
        sendBn.setBackground(Color.decode("#009688"));
        sendBn.setForeground(Color.WHITE);
//        按钮面板
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        btns.setBackground(Color.WHITE);
        btns.add(sendBn);

//        给发送按钮绑定监听事件
        sendBn.addActionListener(e->{
            //获取输入框消息
            String msg = smsSend.getText();
            //清空输入框
            smsSend.setText("");
            //发送方法
            sendMsgToServer(msg);

        });
//        添加组件
        bottomPanel.add(smsSendScrollPane,BorderLayout.CENTER);
        bottomPanel.add(btns,BorderLayout.EAST);
//        用户列表面板
        JScrollPane userListScrollPane = new JScrollPane(onLineUsers);
        userListScrollPane.setBorder(BorderFactory.createEmptyBorder());
        userListScrollPane.setPreferredSize(new Dimension(120,500));
//        中心消息面板
        JScrollPane smsContentScrollPane = new JScrollPane(smsContent);
        smsContentScrollPane.setBorder(BorderFactory.createEmptyBorder());

        this.add(smsContentScrollPane,BorderLayout.CENTER);
        this.add(bottomPanel,BorderLayout.SOUTH);
        this.add(userListScrollPane,BorderLayout.EAST);
    }
//----------------------------将获取的输入框消息发给服务端-------------------------------------
    private void sendMsgToServer(String msg) {
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeInt(2);
            dos.writeUTF(msg);
            dos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//----------------------------将线程读取到的在线用户列表展示出来---------------------------------
    public void UpdateOnlineUsers(String[] userlist) {
        onLineUsers.setListData(userlist);
    }

//---------------------------------更新群聊消息到界面展示-----------------------------------
    public void setMsgToWin(String msg) {
        smsContent.append(msg);
    }
}
