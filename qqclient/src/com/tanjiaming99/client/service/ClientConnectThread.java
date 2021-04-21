package com.tanjiaming99.client.service;

import com.tanjiaming99.common.Message;
import com.tanjiaming99.common.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @Author tanjiaming99.com
 * @Date 2021/4/19 18:12
 **/
public class ClientConnectThread extends Thread {
    /**
     * 必须持有socket才能继续运行
     * 这好像是。。面向对象的思想，我都不知道怎么传这些进来
     */
    private Socket socket;


    @Override
    public void run() {
        // 因为线程需要在后台和服务器通信，因此做成一个无限循环
        while (true) {
            try {
                ObjectInputStream ois = new ObjectInputStream(this.getSocket().getInputStream());
                // 接收对面传来的消息。若无消息传输，会阻塞于此
                Message message = (Message) ois.readObject();
                // 根据类型的不同，作不同的处理
                if (MessageType.MESSAGE_RETURN_ONLINE_FRIENDS.equals(message.getMessageType())) {
                    // 规定用户名列表是以空格分隔，即100 200 300……
                    String[] onlineUsers = message.getContent().split(" ");
                    System.out.println("\n==========当前在线用户列表========");
                    for (int i = 0; i < onlineUsers.length; i++) {
                        System.out.println("用户：" + onlineUsers[i]);
                    }
                } else if (MessageType.MESSAGE_GROUP_PACKAGE.equals(message.getMessageType())) {
                    System.out.println("\n" + message.getSender() + "对大家说：" + message.getContent());
                }else if (MessageType.MESSAGE_OFFLINE_PACKAGE.equals(message.getMessageType())){
                    System.out.println("\n离线消息：" + message.getSender() + "对：" + message.getReceiver() +
                            "说：" + message.getContent());
                }
                else if (MessageType.MESSAGE_COMMON_PACKAGE.equals(message.getMessageType())) {
                    // 显示这些消息即可
                    System.out.println("\n" + message.getSender() + "对：" + message.getReceiver() +
                            "说：" + message.getContent());
                } else {
                    System.out.println("其它类型，暂时不处理");
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }


        }
    }


    /**
     * 构造器传入
     *
     * @param socket
     */
    public ClientConnectThread(Socket socket) {
        this.socket = socket;
    }

    /**
     * 为了更方便的得到socket
     *
     * @return
     */
    public Socket getSocket() {
        return socket;
    }
}
