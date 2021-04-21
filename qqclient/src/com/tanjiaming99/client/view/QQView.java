package com.tanjiaming99.client.view;

import com.tanjiaming99.client.service.MessageService;
import com.tanjiaming99.client.service.UserService;
import com.tanjiaming99.client.utils.ScannerUtil;

import java.util.Scanner;

/**
 * @Author tanjiaming99.com
 * @Date 2021/4/19 16:39
 **/
public class QQView {
    /**
     * 控制是否显示菜单
     */
    private boolean loop = true;
    /**
     * 用于接收用户键盘输入
     */
    private String key = "";
    /**
     * 与用户相关的服务，如登录
     */
    private UserService userService = new UserService();
    /**
     * 与发送消息有关的服务，如私聊、群聊
     */
    private MessageService messageService = new MessageService();

    public static void main(String[] args) {
        new QQView().mainMenu();
    }

    /**
     * 显示主菜单
     */
    private void mainMenu() {
        while (loop) {
            System.out.println("========欢迎登录网络通信系统=========");
            System.out.println("\t\t    1 登录系统");
            System.out.println("\t\t    9 退出系统");
            System.out.print("请输入你的选择：");
            key = ScannerUtil.readString(1);
            switch (key) {
                case "1":
                    loginChat();
                    break;
                case "9":
                    System.out.println("退出系统");
                    loop = false;
                    break;
            }
        }
    }

    public void loginChat(){
        System.out.print("请输入用户号：");
        String userId = ScannerUtil.readString(50);
        System.out.print("请输入用户密码：");
        String password = ScannerUtil.readString(50);
        // 这里就要发送用户和密码到服务端，让它验证该用户是否合法
        if (userService.checkUser(userId, password)) {
            System.out.println("登录成功");
            while (loop) {
                System.out.println("========欢迎！网络通信系统二级菜单（用户" + userId +
                        "）===========");
                System.out.println("\t\t    1 显示在线用户列表");
                System.out.println("\t\t    2 群发消息");
                System.out.println("\t\t    3 私聊消息");
                System.out.println("\t\t    4 离线留言");
                System.out.println("\t\t    9 退出系统");
                System.out.print("请输入你的选择：");
                key = ScannerUtil.readString(1);
                switch (key) {
                    case "1":
                        System.out.println("打印在线的用户");
                        userService.onlineFriendList();
                        break;
                    case "2":
                        System.out.print("请输入想对大家说的话：");
                        String groupContent = ScannerUtil.readString(100);
                        messageService.sendMessageToAll(groupContent, userId);
                        break;
                    case "3":
                        System.out.println("私聊消息");
                        System.out.print("请输出想聊天的用户号（在线）：");
                        String receiverId = ScannerUtil.readString(50);
                        System.out.print("请输入想说的话：");
                        String content = ScannerUtil.readString(100);
                        messageService.sendMessageToOne(content, userId, receiverId);
                        break;
                    case "4":
                        System.out.println("离线留言");
                        System.out.print("请输入想聊天的用户号（离线用户）：");
                        String offlineReceiverId = ScannerUtil.readString(50);
                        System.out.print("请输入想说的话：");
                        String offlineContent = ScannerUtil.readString(100);
                        messageService.offlineMessage(userId, offlineReceiverId, offlineContent);
                        break;
                    case "9":
                        System.out.println("退出系统");
                        // 只是退出while循环，但是线程没有结束
                        loop = false;
                        // 退出
                        userService.logout();
                        break;
                    default:
                        System.out.println("操作成功！");
                        break;
                }
            }
        } else {
            System.out.println("登录失败");
        }
    }
}
