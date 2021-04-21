package com.tanjiaming99.server.service;


import com.tanjiaming99.common.Message;
import com.tanjiaming99.common.MessageType;
import com.tanjiaming99.common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author tanjiaming99.com
 * @Date 2021/4/19 18:53
 * 服务端，监听9999，等待客户端连接，并保持通信
 */
public class QQServer {
    private ServerSocket serverSocket = null;

    /**
     * 使用一个集合存放多个用户，如果是这个用户登录，就认为是合法的
     * 线程安全的hashmap
     */
    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();

    /**
     * 离线消息集合
     */
    public static ConcurrentHashMap<String, ArrayList<Message>> offlineMessageMap = new ConcurrentHashMap<>();

    // 初始化一些用户
    static {
        validUsers.put("100", new User("100", "123456"));
        validUsers.put("200", new User("200", "123456"));
        validUsers.put("300", new User("300", "123456"));
        validUsers.put("至尊宝", new User("至尊宝", "123456"));
        validUsers.put("紫霞仙子", new User("紫霞仙子", "123456"));
    }

    public static void main(String[] args) {
        new QQServer();
    }

    /**
     * 验证用户是否合法，即验证这个用户是否存在
     *
     * @param userId
     * @param password
     * @return
     */
    private boolean checkUser(String userId, String password) {
        User user = validUsers.get(userId);
        // 不存在这个id对应的用户
        if (null == user) {
            return false;
        }
        // 密码错误
        if (!user.getPassword().equals(password)) {
            return false;
        }

        return true;
    }


    public QQServer() {
        try {
            System.out.println("服务端在9999端口监听……");
            // 注：端口可以写在一个配置文件中
            serverSocket = new ServerSocket(9999);
            while (true) {
                // 监听是循环的，一直监听9999端口，因为可能还有别的客户端连接
                // 这块是监听登录的
                Socket socket = serverSocket.accept();
                // 得到socket关联的对象输入输出流
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                // 读取传来的用户，验证登录。
                User user = (User) ois.readObject();
                // 无论登录成功或失败，都会返回消息以回复客户端
                Message message = new Message();
                if (checkUser(user.getUserId(), user.getPassword())) {
                    // 登录成功
                    message.setMessageType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    oos.writeObject(message);
                    // 创建一个线程，和客户端保持通信，
                    ServerConnectThread serverConnectThread = new ServerConnectThread(socket, user.getUserId());
                    serverConnectThread.start();
                    // 加入集合中，方便管理
                    ServerThreadManager.addServerConnectServerThread(user.getUserId(), serverConnectThread);
                } else {
                    message.setMessageType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    oos.close();
                    ois.close();
                    // 登录失败了，就可以关闭
                    socket.close();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // 退出了while循环，就要关闭socket了
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
