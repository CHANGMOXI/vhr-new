package org.changmoxi.vhr.model;

import lombok.Data;

import java.util.Date;

/**
 * @author CZS
 * @create 2023-02-15 17:38
 **/
@Data
public class ChatMessage {
    /**
     * 发送者的用户名，对应Hr的username字段
     */
    private String from;

    /**
     * 发送者的名字，对应Hr的name字段
     */
    private String fromName;

    /**
     * 接收者的用户名，对应Hr的username字段
     */
    private String to;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 发送时间
     */
    private Date sendDate;
}