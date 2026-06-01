package com.idea;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class ServerThread extends Thread{
    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
//            服务端接收的消息有很多种1、登录消息（包含昵称）2、群聊消息3、私聊消息
//            所以服务端必须声明协议发送信息
//            比如客户端发送1代表接下来是登录消息，发送2代表群聊消息，发送3代表的是私聊消息
            DataInputStream ds = new DataInputStream(socket.getInputStream());
            while (true) {
                int type = ds.readInt();
                switch (type){
                    case 1:
                        //代表客户端发来的是登录消息，接下来要接收昵称，更新所有客户端的在线人数
                        String socketname = ds.readUTF();
                        //将获取到的昵称添加到集合中
                        Server.onlineSockets.put(socket,socketname);
                        //更新全部在线客户端的在线人数
                        updateClientOnlineUserList();
                        break;
                    case 2:
                        //代表客户端发来的是群消息，接下来要接收群聊消息，再转发给所有的客户端
                        String msg = ds.readUTF();
                        // 将收到的信息发送给全部客户端
                        sendMsgToAll(msg);
                        break;
                    case 3:
                        //代表客户端发来的是群消息，接下来要接收私聊消息，再转发给指定的客户端
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("客户端下线了："+socket.getInetAddress().getHostAddress() );
            Server.onlineSockets.remove(socket);//用户下线，把socket从集合中移除
            updateClientOnlineUserList();
        }
    }
//-------------------------------将收到的信息发送给全部客户端-------------------------------------
    private void sendMsgToAll(String msg) {
//        拼接信息
        StringBuilder sb = new StringBuilder();
//        获取当前发信息的socket管道对应的昵称
        String name = Server.onlineSockets.get(socket);
//        获取当前时间
        LocalDateTime now = LocalDateTime.now();
//        对当前时间进行格式转换
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss EEE");
        String nowTime = now.format(formatter);
//        将昵称和当前时间进行拼接
        String msgResult = sb.append(name).append(" ").append(nowTime).
                            append("\r\n").append(msg).append("\r\n").toString();
//        遍历获取当前在线的所有socket管道
        for (Socket socket: Server.onlineSockets.keySet()) {
            try {
                //将信息写出去
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeInt(2);//1代表是接下来发送的在线人数类列表信息，2代表是群聊信息
                dos.writeUTF(msgResult);//将信息转发给所有的客户端socket管道
                dos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
//----------------------------更新全部在线客户端的在线人数----------------------------------------
    private void updateClientOnlineUserList() {
        //拿到所有的在线的客户端昵称，通过socket管道转发给所有的客户端
        //1.拿到当前在线的用户昵称
        Collection<String> onlineUsers = Server.onlineSockets.values();
        //2、遍历所有的管道，把所有的在线人数昵称遍历写出去，写给每一个在线的socket管道
        for (Socket socket: Server.onlineSockets.keySet()){
            try {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeInt(1);//1代表是接下来发送的在线人数类列表信息，2代表是群聊
                dos.writeInt(onlineUsers.size());//告诉客户端接下来要发送多少个用户昵称
                for (String user : onlineUsers){//将所有的在线昵称写出去
                    dos.writeUTF(user);
                }
                dos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
