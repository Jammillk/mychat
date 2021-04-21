package com.tanjiaming99.server.service;

import com.tanjiaming99.common.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author tanjiaming99.com
 * @Date 2021/4/19 19:11
 **/

/**
 * 服务器线程管理
 * 这里存放着所有连接上服务器的线程
 * @author JiaMing
 */
public class ServerThreadManager {
    private static HashMap<String , ServerConnectThread> serverThreadMap = new HashMap<>();


    public static void addServerConnectServerThread(String userId, ServerConnectThread serverConnectClientThread){
        serverThreadMap.put(userId,serverConnectClientThread);
    }
    public static void  removeServerConnectServerThread(String userId){
        serverThreadMap.remove(userId);
    }

    public static ServerConnectThread getServerThread(String userId){
        return serverThreadMap.get(userId);
    }

    /**
     * 返回在线用户列表
     */
    public static String getOnlineUser(){
        // 遍历hashmap的key，得到它们的用户名，然后拼接
        Iterator<String> iterator = serverThreadMap.keySet().iterator();
        String onlineUserList = "";
        while (iterator.hasNext()){
            onlineUserList += iterator.next() + " ";
        }
        return onlineUserList;
    }

    public static HashMap<String, ServerConnectThread> getServerThreadMap() {
        return serverThreadMap;
    }
}
