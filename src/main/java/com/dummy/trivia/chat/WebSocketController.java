//package com.dummy.trivia.chat;
//
//import lombok.Data;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Controller;
//
//@Component
//@Data
//public class WebSocketController {
//
//    @MessageMapping(value = "/welcome")
//    @SendTo("/topic/getResponse") //表示当服务器有消息需要推送时，会对订阅了@SendTo中路径的浏览器广播发送消息
//    public ResponseMessage say(RequestMessage message) {
//        System.out.println(message.getName());
//        return new ResponseMessage("Welcome, " + message.getName() + " !");
//    }
//}
