package com.tanjiaming99.client.service;


import com.tanjiaming99.common.Message;
import com.tanjiaming99.common.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

/**
 * @Author tanjiaming99.com
 * @Date 2021/4/20 09:10
 * 提供和消息相关的服务方法
 */
public class MessageService {
    /**
     * 发送消息给某人
     *
     * @param content    消息内容
     * @param senderId   发送者
     * @param receiverId 接收者
     */
    public void sendMessageToOne(String content, String senderId, String receiverId) {
        // 把内容封装成一个Message，然后发送给服务器
        Message message = new Message();
        message.setSender(senderId);
        message.setReceiver(receiverId);
        message.setContent(content);
        message.setSendTime(new Date().toString());
        message.setMessageType(MessageType.MESSAGE_COMMON_PACKAGE);
        System.out.println(senderId + "对" + receiverId + "说：" + content);
        sendMessage(message);
    }

    /**
     * 发送消息给所有人，即群聊
     *
     * @param content
     * @param senderId
     */
    public void sendMessageToAll(String content, String senderId) {
        Message message = new Message();
        message.setContent(content);
        message.setSender(senderId);
        message.setSendTime(new Date().toString());
        message.setMessageType(MessageType.MESSAGE_GROUP_PACKAGE);

        System.out.println(senderId + "对大家说：" + content);
        sendMessage(message);
    }

    /**
     * 发送离线消息
     *
     * @param userId
     * @param offlineReceiverId
     * @param offlineContent
     */
    public void offlineMessage(String userId, String offlineReceiverId, String offlineContent) {
        Message message = new Message();
        message.setSender(userId);
        message.setReceiver(offlineReceiverId);
        message.setContent(offlineContent);
        message.setSendTime(new Date().toString());
        message.setMessageType(MessageType.MESSAGE_OFFLINE_PACKAGE);

        System.out.println("客户端："+userId+"发送了一条离线消息给"+offlineReceiverId+" 内容是：" + offlineContent);
        sendMessage(message);
    }

    /**
     * 发送消息，整合一下。。
     *
     * @param message
     */
    public void sendMessage(Message message) {
        // 发送给客户端
        try {
            // 得到当前线程的socket对应的objectOutputStream对象
            // 这是从管理的集合中得到线程
            // 还是这样拆开看比较好
            ClientConnectThread clientThread = ClientThreadManager.getClientThread(message.getSender());
            Socket socket = clientThread.getSocket();
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            // 发送这个对象出去
            oos.writeObject(message);
            // 这行有问题！好像不能随便关，
            //oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
