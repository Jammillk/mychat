package com.tanjiaming99.client.service;

import com.tanjiaming99.common.Message;
import com.tanjiaming99.common.MessageType;
import com.tanjiaming99.common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @Author tanjiaming99.com
 * @Date 2021/4/19 17:07
 * 该类完成用户登录验证和用户注册等等功能
 **/
public class UserService {
    /**
     * 用户，我自己
     */
    private User user = new User();
    /**
     * socket同理，也可能在别的地方使用
     */
    private Socket socket;

    private static final String HOSTNAME = "127.0.0.1";

    private static final int PORT = 9999;

    /**
     * 根据用户id和密码验证该用户是否合法
     * 把user对象发送到服务器，检查其返回类型
     * 若成功，则允许登录，否则退出
     * @param userId
     * @param password
     * @return
     */
    public boolean checkUser(String userId, String password) {
        // 完善user对象
        user.setUserId(userId);
        user.setPassword(password);
        try {
            // 发送user对象以验证用户是否合法
            socket = new Socket(InetAddress.getByName(HOSTNAME), PORT);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(user);

            // 接收从服务器回复的Message对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            // 知其传输的一定是Message，所以可以直接强制，以后拓展了再加
            Message msg = (Message) ois.readObject();
            // 根据类型判断
            if (MessageType.MESSAGE_LOGIN_SUCCEED.equals(msg.getMessageType())) {
                // 登录成功，启动线程，可以开始聊天了
                ClientConnectThread clientConnectThread = new ClientConnectThread(socket);
                clientConnectThread.start();
                // 一个客户端可以有多个线程与他人聊天
                ClientThreadManager.addThread(userId, clientConnectThread);


                return true;
            } else if (MessageType.MESSAGE_LOGIN_FAIL.equals(msg.getMessageType())) {
                // 登录失败，不启动和服务器通信的线程，关闭socket
                System.out.println("登录失败，关闭socket，准备退出");
                socket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 向服务器端请求获取在线用户列表
     */
    public void onlineFriendList() {
        Message message = new Message();
        // 设置发送消息的类型，即向服务器说明你想做的事
        message.setMessageType(MessageType.MESSAGE_GET_ONLINE_FRIENDS);
        sendMessage(message);
    }

    /**
     * 编写方法，退出客户端，并向服务器发送退出信息
     */
    public void logout() {
        Message message = new Message();
        // 退出消息类型
        message.setMessageType(MessageType.MESSAGE_CLIENT_EXIT);
        sendMessage(message);
        System.out.println(user.getUserId()+" 退出系统");
        System.exit(0);
    }

    /**
     * 发送消息给服务端
     * @param message
     */
    public void sendMessage(Message message) {
        // 发送前，还要设置发送者，即自己
        message.setSender(user.getUserId());
        // 发送给服务器
        try {
            // 得到当前线程的socket对应的objectOutputStream对象
            // 这是从管理的集合中得到线程
            // 还是这样拆开看比较好
            ClientConnectThread clientThread = ClientThreadManager.getClientThread(user.getUserId());
            Socket socket = clientThread.getSocket();
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            // 发送这个对象出去
            oos.writeObject(message);
            // 它不能关，关了会把整个socket关掉，但是不关的话又显得浪费？
//            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
