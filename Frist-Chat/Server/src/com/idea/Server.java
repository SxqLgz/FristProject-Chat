package com.idea;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
//=============================服务端=======================================
public class Server {
//  定义一个集合，只要一个，用来存放所有登录的socket管道，以便将消息转发给所有登录的客户端
    public static final Map<Socket,String> onlineSockets = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("服务端系统启动.....");
        try {
//          1、注册端口
            ServerSocket serverSocket = new ServerSocket(Constant.PORT);
//            2、主线程负责接收客户端的连接请求
            while (true) {
//               3、调用accept方法，获取客户端的Socket对象
                System.out.println("等待客户端连接");

                Socket socket = serverSocket.accept();
 //              4、把管道交给一个独立的线程处理，以便可以支持很多客户端同时通信
                new ServerThread(socket).start();

                System.out.println("一个客户端连接成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
