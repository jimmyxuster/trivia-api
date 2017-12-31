package com.dummy.trivia.socket;

import com.dummy.trivia.db.model.Player;
import com.dummy.trivia.db.model.Question;
import com.dummy.trivia.db.model.Room;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/websocket")
@Component
@Data
public class MyWebSocket {

    private static DateFormat dateFormat = new SimpleDateFormat("HH:mm:SS");
    private static final Logger LOGGER = LoggerFactory.getLogger(MyWebSocket.class);
    private static int onlineCount = 0;
    private static CopyOnWriteArraySet<MyWebSocket> webSocketSet = new CopyOnWriteArraySet<>();
    private static List<Room> rooms = new ArrayList<>();
    private Session session;

    /**
     * 获取在线人数
     *
     * @return 在线人数
     */
    private static synchronized int getOnlineCount() {
        return MyWebSocket.onlineCount;
    }

    /**
     * 添加在线人数
     */
    private static synchronized void addOnlineCount() {
        MyWebSocket.onlineCount++;
    }

    /**
     * 减少在线人数
     */
    private static synchronized void subOnlineCount() {
        MyWebSocket.onlineCount--;
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
        JsonParser parser =new JsonParser();  //创建json解析器
        JsonObject json = (JsonObject) parser.parse(message);

        String type = json.get("type").getAsString();
        switch (type) {
            case "answer":
                String choice = json.get("choice").getAsString();
                System.out.println("Choice: " + choice);
                for (MyWebSocket item : webSocketSet) {
                    item.sendMessage("选择的选项是" + choice);
                }
                return choice;
            default:
                System.out.println("无法解析的消息类型");
                for (MyWebSocket item : webSocketSet) {
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
