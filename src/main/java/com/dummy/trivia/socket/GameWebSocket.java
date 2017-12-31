package com.dummy.trivia.socket;

import com.dummy.trivia.db.model.Player;
import com.dummy.trivia.db.model.Question;
import com.dummy.trivia.db.model.Room;
import com.dummy.trivia.util.GameMessageJsonHelper;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 把今天最好的表现当作明天最新的起点．．～
 * いま 最高の表現 として 明日最新の始発．．～
 * Today the best performance  as tomorrow newest starter!
 * Created by IntelliJ IDEA.
 * <p>
 *
 * @author : xiaomo
 * github: https://github.com/xiaomoinfo
 * email: xiaomo@xiaomo.info
 * <p>
 * Date: 2016/11/3 16:36
 * Description: 用户实体类
 * Copyright(©) 2015 by xiaomo.
 **/

@ServerEndpoint("/websocket")
@Component
public class GameWebSocket {

//    private static DateFormat dateFormat = new SimpleDateFormat("HH:mm:SS");
    private static final Logger LOGGER = LoggerFactory.getLogger(GameWebSocket.class);
    private static int onlineCount = 0;
    private static CopyOnWriteArraySet<GameWebSocket> webSocketSet = new CopyOnWriteArraySet<>();
    private static List<Room> rooms = new ArrayList<>();
    private Session session;

    /**
     * 获取在线人数
     *
     * @return 在线人数
     */
    private static synchronized int getOnlineCount() {
        return GameWebSocket.onlineCount;
    }

    /**
     * 添加在线人数
     */
    private static synchronized void addOnlineCount() {
        GameWebSocket.onlineCount++;
    }

    /**
     * 减少在线人数
     */
    private static synchronized void subOnlineCount() {
        GameWebSocket.onlineCount--;
    }

    /**
     * 有人进入游戏
     *
     * @param session session
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);
        addOnlineCount();
        LOGGER.info("有新用户加入!当前在线人数为:{}", getOnlineCount());
    }

    /**
     * 有人离开游戏
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        subOnlineCount();
        System.out.println("有一用户关闭!当前在线人数为" + getOnlineCount());
    }

    /**
     * 发消息
     *
     * @param message message
     * @throws IOException IOException
     */
//    @OnMessage
//    public void onMessage(String message) throws IOException {
//        String date = "<font color='green'>" + dateFormat.format(new Date()) + "</font></br>";
//        // 群发消息
//        for (MyWebSocket item : webSocketSet) {
//            item.sendMessage(date + message);
//        }
//        LOGGER.info("客户端消息:{}", message);
//
//    }

    @OnMessage
    public String onMessage(String message) throws IOException {
        JsonObject json = GameMessageJsonHelper.parse(message);

        String type = json.get("type").getAsString();
        switch (type) {
            case "answer":
                String choice = json.get("choice").getAsString();
                System.out.println("Choice: " + choice);
                for (GameWebSocket item : webSocketSet) {
                    item.sendMessage("选择的选项是" + choice);
                }
                return choice;
            default:
                System.out.println("无法解析的消息类型");
                for (GameWebSocket item : webSocketSet) {
                    item.sendMessage("无法解析的消息类型");
                }
                return null;
        }
    }

    /**
     * 发送消息
     *
     * @param message message
     * @throws IOException IOException
     */
    private void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    private void answerCorrect(String choice, Player player, Question question) {

    }


}
