//package com.dummy.trivia.chat;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//
//
////socket核心配置容器
//@Configuration
//@EnableWebSocketMessageBroker //表示开启使用STOMP协议来传输基于代理的消息，Broker是代理
//public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
//
//    @Override
//    //配置消息代理
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        //服务器广播消息的基础路径
//        config.enableSimpleBroker("/topic");
//        //客户端订阅消息的基础路径
////        config.setApplicationDestinationPrefixes("/app");
//    }
//
//    @Override
//    //表示注册STOMP协议的节点，并指定映射的URL
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/websocket").withSockJS();
//    }
//
//
//}