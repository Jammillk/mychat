package com.tanjiaming99.client.service;

import java.util.HashMap;

/**
 * @Author tanjiaming99.com
 * @Date 2021/4/19 18:25
 * 管理客户端连接线程
 **/
public class ClientThreadManager {
    /**
     * 把客户端线程放入到HashMap集合中，key是用户id，value是线程
     */
    private static HashMap<String, ClientConnectThread> clientThreadMap = new HashMap<>();


    public static void addThread(String userId, ClientConnectThread clientConnectServerThread) {
        clientThreadMap.put(userId, clientConnectServerThread);
    }

    public static ClientConnectThread getClientThread(String userId) {
        return clientThreadMap.get(userId);
    }
}
