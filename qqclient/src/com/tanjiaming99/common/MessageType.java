package com.tanjiaming99.common;

/**
 * @Author tanjiaming99.com
 * @Date 2021/4/19 16:35
 * 不同的常量值表示不同的消息类型
 **/
public interface MessageType {
    /**
     * 登录成功
     */
    String MESSAGE_LOGIN_SUCCEED = "1";
    /**
     * 登录失败
     */
    String MESSAGE_LOGIN_FAIL = "2";

    /**
     * 普通信息包
     */
    String MESSAGE_COMMON_PACKAGE = "3";

    /**
     * 要求返回在线用户列表
     */
    String MESSAGE_GET_ONLINE_FRIENDS = "4";

    /**
     * 返回在线用户列表
     */
    String MESSAGE_RETURN_ONLINE_FRIENDS = "5";

    /**
     * 退出
     */
    String MESSAGE_CLIENT_EXIT = "6";

    /**
     * 群发消息
     */
    String MESSAGE_GROUP_PACKAGE = "7";
    /**
     * 离线消息包
     */
    String MESSAGE_OFFLINE_PACKAGE = "8";
}
