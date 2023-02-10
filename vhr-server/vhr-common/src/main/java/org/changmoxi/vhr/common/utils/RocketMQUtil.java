package org.changmoxi.vhr.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CZS
 * @create 2023-02-10 12:58
 **/
@Slf4j
@Component
public class RocketMQUtil {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送普通消息
     *
     * @param destination topic 或 topic:tag
     * @param message     消息体
     * @return
     */
    public boolean sendMessage(String destination, Object message) {
        rocketMQTemplate.convertAndSend(destination, message);
        log.info("发送普通消息成功: message = {}", message);
        return true;
    }

    /**
     * 同步发送消息
     *
     * @param destination topic 或 topic:tag
     * @param message     消息体
     * @return
     */
    public boolean syncSendMessage(String destination, Object message) {
        SendResult sendResult = rocketMQTemplate.syncSend(destination, message);
        if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
            log.info("同步发送消息成功: message = {}, sendResult = {}", message, sendResult);
            return true;
        } else {
            log.error("同步发送消息失败: message = {}, sendResult = {}", message, sendResult);
            return false;
        }
    }

    /**
     * 同步发送批量消息
     *
     * @param destination topic 或 topic:tag
     * @param messages    消息体集合
     * @return
     */
    public boolean syncSendBatchMessages(String destination, List<?> messages) {
        List<Message<Object>> messageList = new ArrayList<>();
        for (Object message : messages) {
            messageList.add(MessageBuilder.withPayload(message).build());
        }
        SendResult sendResult = rocketMQTemplate.syncSend(destination, messageList);
        if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
            log.info("同步发送批量消息成功: messages = {}, sendResult = {}", messages, sendResult);
            return true;
        } else {
            log.error("同步发送批量消息失败: messages = {}, sendResult = {}", messages, sendResult);
            return false;
        }
    }

    /**
     * 同步发送顺序消息
     *
     * @param destination topic 或 topic:tag
     * @param messages    消息体集合
     * @param hashKey     同一个hashKey的消息放到同一个消息队列
     */
    public boolean syncSendMessageOrderly(String destination, List<?> messages, String hashKey) {
        List<Message<Object>> messageList = new ArrayList<>();
        for (Object message : messages) {
            messageList.add(MessageBuilder.withPayload(message).build());
        }
        SendResult sendResult = rocketMQTemplate.syncSendOrderly(destination, messageList, hashKey);
        if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
            log.info("同步发送顺序消息成功: messages = {}, hashKey = {}, sendResult = {}", messages, hashKey, sendResult);
            return true;
        } else {
            log.error("同步发送顺序消息失败: messages = {}, hashKey = {}, sendResult = {}", messages, hashKey, sendResult);
            return false;
        }
    }

    /**
     * 同步发送延迟消息
     *
     * @param destination topic 或 topic:tag
     * @param message     消息体
     * @param timeout     超时时间
     * @param delayLevel  开源版RocketMQ只支持固定的延迟级别，从 1 到 18 分别为 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     * @return
     */
    public boolean syncSendDelayMessage(String destination, Object message, long timeout, int delayLevel) {
        SendResult sendResult = rocketMQTemplate.syncSend(destination, MessageBuilder.withPayload(message).build(), timeout, delayLevel);
        if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
            log.info("同步发送延迟消息成功: message = {}, timeout = {}, delayLevel = {}, sendResult = {}", message, timeout, delayLevel, sendResult);
            return true;
        } else {
            log.error("同步发送延迟消息失败: message = {}, timeout = {}, delayLevel = {}, sendResult = {}", message, timeout, delayLevel, sendResult);
            return false;
        }
    }

    /**
     * 异步发送消息
     *
     * @param destination topic 或 topic:tag
     * @param message     消息体
     */
    public void asyncSendMessage(String destination, Object message) {
        SendResult sendResult = new SendResult();
        rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步发送消息成功: message = {}, sendResult = {}", message, sendResult);
            }

            @Override
            public void onException(Throwable e) {
                log.error("异步发送消息失败: message = {}, exception = {}", message, e.getMessage());
            }
        });
    }

    public void asyncSendDelayMessage(String destination, Object message, long timeout, int delayLevel) {
        rocketMQTemplate.asyncSend(destination, MessageBuilder.withPayload(message).build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步发送延迟消息成功: message = {}, timeout = {}, delayLevel = {}, sendResult = {}", message, timeout, delayLevel, sendResult);
            }

            @Override
            public void onException(Throwable e) {
                log.error("异步发送延迟消息失败: message = {}, timeout = {}, delayLevel = {}, exception = {}", message, timeout, delayLevel, e.getMessage());
            }
        }, timeout, delayLevel);
    }

    /**
     * 单向发送消息
     *
     * @param destination topic 或 topic:tag
     * @param message     消息体
     */
    public void sendOneWayMessage(String destination, Object message) {
        rocketMQTemplate.sendOneWay(destination, message);
        log.info("单向发送消息成功: message = {}", message);
    }

    /**
     * 单向发送顺序消息
     *
     * @param destination topic 或 topic:tag
     * @param messages    消息体集合
     * @param hashKey     同一个hashKey的消息放到同一个消息队列
     */
    public void sendOneWayMessageOrderly(String destination, List<?> messages, String hashKey) {
        for (Object message : messages) {
            rocketMQTemplate.sendOneWayOrderly(destination, message, hashKey);
            log.info("单向发送顺序消息成功: message = {}, hashKey = {}", message, hashKey);
        }
    }

    /**
     * 发送事务消息（half消息）
     *
     * @param destination topic 或 topic:tag
     * @param message     消息体
     * @param arg         额外参数，在本地事务监听器可以获取到
     * @return
     */
    public boolean sendMessageInTransaction(String destination, Message<?> message, Object arg) {
        TransactionSendResult sendResult = rocketMQTemplate.sendMessageInTransaction(destination, message, arg);
        if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
            log.info("发送事务消息（half消息）成功: message = {}, sendResult = {}", message, sendResult);
            return true;
        } else {
            log.error("发送事务消息（half消息）失败: message = {}, sendResult = {}", message, sendResult);
            return false;
        }
    }
}