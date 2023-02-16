package org.changmoxi.vhr.controller.ws;

import org.changmoxi.vhr.model.ChatMessage;
import org.changmoxi.vhr.model.Hr;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.Date;

/**
 * WebSocket消息处理类
 *
 * @author CZS
 * @create 2023-02-15 17:36
 **/
@Controller
public class WebSocketController {
    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 消息处理和转发
     *
     * @param authentication 获取当前登录的用户信息，也就是 (Hr) authentication.getPrincipal()，也可以用 HrUtil 来获取
     * @param chatMessage    聊天信息
     */
    @MessageMapping("/ws/chat")// 客户端给服务端发消息的地址
    public void handleMessage(Authentication authentication, ChatMessage chatMessage) {
        // 也可以用 HrUtil 获取当前登录的用户信息
//        Hr currentHr = HrUtil.getCurrentHr();
        Hr currentHr = (Hr) authentication.getPrincipal();
        // 不能用前端设置好的发送者和发送时间，有可能会被冒充，不安全
        chatMessage.setFrom(currentHr.getUsername());
        chatMessage.setFromName(currentHr.getName());
        chatMessage.setSendDate(new Date());
        // 服务端使用Spring封装的simpleMessagingTemplate发送消息，转发到 /queue/chat，监听这个地址的客户端(接收者to)就能接收服务端发送的消息
        // 单聊 一般用 simpMessagingTemplate 这种方式发送，因为需要发送给某个用户
        // 群聊 既可以用这种方式也可以用@SendTo("/xxx/yyy")方式
        simpMessagingTemplate.convertAndSendToUser(chatMessage.getTo(), "/queue/chat", chatMessage);
    }
}