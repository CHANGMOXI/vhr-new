package org.changmoxi.vhr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author CZS
 * @create 2023-02-15 17:29
 **/
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /**
     * 添加建立连接的Endpoint
     *
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 定义一个前缀为 /ws/ep 的Endpoint，开启SockJS的支持(解决浏览器对WebSocket兼容性的问题)，新版SpringSecurity需要指定允许客户端(前端)连接WebSocket的域
        // 所有涉及WebSocket的连接同一加上 /ws 前缀，方便前端把WebSocket请求和其他Http请求区分开
        // 客户端想要跟服务端这个Endpoint建立连接，就得主动发送带有/ws/ep前缀的请求，也就是说 /ws/ep 是建立连接的地址
        registry.addEndpoint("/ws/ep").setAllowedOrigins("http://localhost:8080").withSockJS();
    }

    /**
     * 配置消息代理
     *
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 单聊
        // /queue用于单聊，接收到带有/queue前缀的消息，就会把消息转发给消息代理Broker，后面在controller中根据需要，消息代理再把这条消息广播到 /queue/xxx
        // 比如WebSocketController中的handleMessage方法调用 simpMessagingTemplate 将消息发送(广播)到 /queue/chat，客户端监听/queue/chat就能收到服务端发送的消息
        registry.enableSimpleBroker("/queue");
    }
}