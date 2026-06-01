package com.idea.UI;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientReaderThread extends Thread{
    private Socket socket;
    private DataInputStream ds;
    private ClientChatFrame win;

    public ClientReaderThread(Socket socket, ClientChatFrame win) {
        this.socket = socket;
        this.win = win;
    }
    @Override
    public void run() {
        try {
//            服务端接收的消息有很多种1、在线人数更新的数据2、群聊消息
            ds = new DataInputStream(socket.getInputStream());
            while (true) {
                int type = ds.readInt();
                switch (type){
                    case 1:
                        //代表服务端发来的在线人数
                        UpdateClientOnlineUser();
                        break;
                    case 2:
                        //代表服务端发来的是群消息
                        geyMsgToWin();
                        break;
                }
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    private void geyMsgToWin() throws Exception {
        String msg = ds.readUTF();
        win.setMsgToWin(msg);
    }

    //-------------------------------更新客户端在线列表----------------------------------------
    private void UpdateClientOnlineUser() throws Exception {
        //1、和服务端发送的数据对应，先读取有多少课在线用户
        int count = ds.readInt();
        //2、循环读取用户的信息
        String[] userlist = new String[count];
        for (int i = 0; i < count; i++) {
            String nickname = ds.readUTF();
            //3、将每个用户信息添加到一个数组中
            userlist[i] = nickname;
        }
        //4、将在线人数展示到窗口
        win.UpdateOnlineUsers(userlist);
    }
}
