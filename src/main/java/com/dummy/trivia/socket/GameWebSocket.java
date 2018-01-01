package com.dummy.trivia.socket;

import com.dummy.trivia.config.Config;
import com.dummy.trivia.db.model.Player;
import com.dummy.trivia.db.model.Question;
import com.dummy.trivia.db.model.base.BaseGameResponse;
import com.dummy.trivia.db.model.game.Answer;
import com.dummy.trivia.service.IQuestionService;
import com.dummy.trivia.service.impl.QuestionService;
import com.dummy.trivia.util.GameGsonUtil;
import com.dummy.trivia.util.GameMessageJsonHelper;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value="/websocket")
@Component
public class GameWebSocket extends TextWebSocketHandler {

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

//    private static DateFormat dateFormat = new SimpleDateFormat("HH:mm:SS");
    private static final Logger LOGGER = LoggerFactory.getLogger(GameWebSocket.class);
    private static int onlineCount = 0;
    private static CopyOnWriteArraySet<GameWebSocket> webSocketSet = new CopyOnWriteArraySet<>();
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
                handleAnswer(choice);
                return null;
            default:
                System.out.println("无法解析的消息类型");
                handleAnswer("无法解析的消息类型");
                return null;
        }
    }

    private void handleAnswer(String s) throws IOException {
        IQuestionService questionService = applicationContext.getBean(QuestionService.class);
        Answer answer = questionService.attemptAnswer(s);
        BaseGameResponse response;
        if (answer == null) {
            response = BaseGameResponse.bad(Config.GAME_MSG_TYPE_ANSWER,
                    -100, "没有正在进行的题目或作答格式错误");
        } else {
            response = BaseGameResponse.good(Config.GAME_MSG_TYPE_ANSWER, answer);
        }
        String responseMsg = GameGsonUtil.convertToJson(response);
        for (GameWebSocket item : webSocketSet) {
            item.sendMessage(responseMsg);
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
