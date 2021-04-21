package com.tanjiaming99.common;

import java.io.Serializable;

/**
 * @Author tanjiaming99.com
 * @Date 2021/4/19 16:28
 * 客户端和服务端通信时的消息对象
 **/
public class Message implements Serializable  {
    private static final long serialVersionUID = 1L;
    /**
     * 发送者
     */
    private String sender;
    /**
     * 接收者
     */
    private String receiver;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 发送时间
     */
    private String sendTime;
    /**
     * 消息类型
     * （是聊天，还是发文件？等……，可在接口定义消息类型）
     */
    private String messageType ;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }
}
