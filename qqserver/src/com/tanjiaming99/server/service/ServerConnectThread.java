package com.tanjiaming99.server.service;

import com.tanjiaming99.common.Message;
import com.tanjiaming99.common.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author tanjiaming99.com
 * @Date 2021/4/19 19:02
 * 该类某个对应的对象和某个客户端保持通信
 */
public class ServerConnectThread extends Thread {
    /**
     * 与客户端连接的线程
     */
    private Socket socket;
    /**
     * 与哪个客户端相通信
     */
    private String userId;

    private boolean flag = true;

    /**
     * 发送或接收消息
     */
    @Override
    public void run() {
        checkOfflineMessage(socket, userId);
        while (true) {
            System.out.println("服务端与客户端" + userId +
                    "保持通信，读取数据ing。。。");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                // 根据客户端的服务进行对应的操作
                if (MessageType.MESSAGE_GET_ONLINE_FRIENDS.equals(message.getMessageType())) {
                    System.out.println(message.getSender() + " 要在线用户列表");
                    // 要求在线用户列表，即获取所有连接到服务器的线程的key
                    String onlineUser = ServerThreadManager.getOnlineUser();
                    Message msg = new Message();
                    msg.setContent(onlineUser);
                    msg.setMessageType(MessageType.MESSAGE_RETURN_ONLINE_FRIENDS);
                    // 谁发的就传回去给谁
                    msg.setReceiver(message.getSender());
                    ObjectOutputStream oos = new ObjectOutputStream(this.socket.getOutputStream());
                    oos.writeObject(msg);
                } else if (MessageType.MESSAGE_COMMON_PACKAGE.equals(message.getMessageType())) {
                    // 得到接收者的线程
                    ServerConnectThread serverThread = ServerThreadManager.getServerThread(message.getReceiver());
                    // 根据它的socket，转发出去
                    ObjectOutputStream oos = new ObjectOutputStream(serverThread.getSocket().getOutputStream());
                    // 若其不在线，可保存到数据库
                    oos.writeObject(message);
                } else if (MessageType.MESSAGE_GROUP_PACKAGE.equals(message.getMessageType())) {
                    HashMap<String, ServerConnectThread> serverThreadMap = ServerThreadManager.getServerThreadMap();
                    Iterator<String> iterator = serverThreadMap.keySet().iterator();
                    while (iterator.hasNext()) {
                        String onlineUserId = iterator.next();
                        // 除了自己，别人都要收到
                        if (!onlineUserId.equals(message.getSender())) {
                            // 根据它的socket，转发出去
                            ObjectOutputStream oos = new ObjectOutputStream(serverThreadMap.get(onlineUserId).getSocket().getOutputStream());
                            // 若其不在线，可保存到数据库
                            oos.writeObject(message);
                        }
                    }
                } else if (MessageType.MESSAGE_OFFLINE_PACKAGE.equals(message.getMessageType())) {
                    // 把得到的离线消息包全部加进去，等它一上线就发送
                    ConcurrentHashMap<String, ArrayList<Message>> offlineMessageMap = QQServer.offlineMessageMap;
                    String receiverId = message.getReceiver();
                    // 已有了，就把多条消息放入
                    if (offlineMessageMap.containsKey(receiverId)) {
                        ArrayList<Message> messages = offlineMessageMap.get(receiverId);
                        messages.add(message);
                    } else {
                        // 否则就加入新消息
                        ArrayList<Message> messages = new ArrayList<>();
                        messages.add(message);
                        offlineMessageMap.put(message.getReceiver(), messages);
                    }
                    System.out.println("服务器接收到来自" + message.getSender() +
                            "的离线消息，发送给" + message.getReceiver() +
                            "。内容为：" + message.getContent());
                } else if (MessageType.MESSAGE_CLIENT_EXIT.equals(message.getMessageType())) {
                    System.out.println(userId + "要退出系统了");
                    // 把自己从集合管理中移除
                    ServerThreadManager.removeServerConnectServerThread(message.getSender());
                    // 关闭连接
                    socket.close();
                    // 退出循环
                    break;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkOfflineMessage(Socket socket, String userId) {
        ConcurrentHashMap<String, ArrayList<Message>> offlineMessageMap = QQServer.offlineMessageMap;
        if (offlineMessageMap.containsKey(userId)) {
            ArrayList<Message> messages = offlineMessageMap.get(userId);
            for (Message message : messages) {
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 发完就删除它
            offlineMessageMap.remove(userId);
        } else {
            System.out.println("经检查，无用户" + userId +
                    "的离线消息。");
        }
    }

    public ServerConnectThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket() {
        return socket;
    }
}
