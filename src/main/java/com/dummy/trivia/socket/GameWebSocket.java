package com.dummy.trivia.socket;

import com.dummy.trivia.config.Config;
import com.dummy.trivia.db.model.Player;
import com.dummy.trivia.db.model.Question;
import com.dummy.trivia.db.model.Room;
import com.dummy.trivia.db.model.User;
import com.dummy.trivia.db.model.base.BaseGameResponse;
import com.dummy.trivia.db.model.game.Answer;
import com.dummy.trivia.service.IGameService;
import com.dummy.trivia.service.IQuestionService;
import com.dummy.trivia.service.IUserService;
import com.dummy.trivia.service.impl.GameService;
import com.dummy.trivia.service.impl.QuestionService;
import com.dummy.trivia.service.impl.UserService;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/websocket")
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

    private String username;
    private long roomName;

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
        removeUserFromRoom();
    }

    private void removeUserFromRoom() {
        if (this.username != null && this.roomName > 0) {
            try {
                handleExitRoom(username, roomName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            case "joinRoom":
                String joinUsername = json.get("username").getAsString();
                long joinRoomName = json.get("roomName").getAsLong();
                setUsernameAndRoom(joinUsername, joinRoomName);
                BaseGameResponse response = handleJoinRoom(joinUsername, joinRoomName);
                if (response != null) {
                    return GameMessageJsonHelper.convertToJson(response);
                }
                return null;
            case "exitRoom":
                String exitUsername = json.get("username").getAsString();
                long exitRoomName = json.get("roomName").getAsLong();
                handleExitRoom(exitUsername, exitRoomName);
                return null;
            case "ready":
                String readyUsername = json.get("username").getAsString();
                long readyRoomName = json.get("roomName").getAsLong();
                setUsernameAndRoom(readyUsername, readyRoomName);
                BaseGameResponse errorReady = handleReady(readyUsername, readyRoomName);
                return errorReady == null ? null : GameMessageJsonHelper.convertToJson(errorReady);
            case "startGame":
                String startGameUsername = json.get("username").getAsString();
                long startGameRoomName = json.get("roomName").getAsLong();
                setUsernameAndRoom(startGameUsername, startGameRoomName);
                BaseGameResponse errorStartGame = handleStartGame(startGameUsername, startGameRoomName);
                return errorStartGame == null ? null : GameMessageJsonHelper.convertToJson(errorStartGame);
            default:
                handleUnknown();
                return null;
        }
    }

    private void handleUnknown() throws IOException {
        BaseGameResponse response = BaseGameResponse.bad(Config.GAME_MSG_TYPE_UNKNOWN, -200, "无法解析的消息类型");
        String responseMsg = GameMessageJsonHelper.convertToJson(response);
        for (GameWebSocket item : webSocketSet) {
            item.sendMessage(responseMsg);
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
        String responseMsg = GameMessageJsonHelper.convertToJson(response);
        for (GameWebSocket item : webSocketSet) {
            item.sendMessage(responseMsg);
        }
    }

    private BaseGameResponse handleJoinRoom(String username, long roomName) throws IOException {
        BaseGameResponse response = null;
        IUserService userService = applicationContext.getBean(UserService.class);
        IGameService gameService = applicationContext.getBean(GameService.class);
        User user = userService.getUserInfo(username);
        Room room = gameService.getRoomInfo(roomName);
        if (user != null && room != null && room.getStatus().equals("Avail")) {
            if (room.getPlayers().size() >= 4) {
                return BaseGameResponse.bad(Config.GAME_MSG_TYPE_JOIN_ROOM, -104, "房间已满");
            } else {
                // 如果是房主，在创建房间时就已加入房间（防止房主被别人占位）
                if (!username.equals(room.getOwnerName())) {
                    for (User u : room.getPlayers()) {
                        if (u.getUsername().equals(username)) {
                            return BaseGameResponse.bad(Config.GAME_MSG_TYPE_JOIN_ROOM, -103, "玩家已经在房间内");
                        }
                    }
                    room.addPlayer(user);
                }
                Room savedRoom = gameService.saveRoom(room);
                response = BaseGameResponse.good(Config.GAME_MSG_TYPE_JOIN_ROOM, savedRoom);
            }

        } else {
            return BaseGameResponse.bad(Config.GAME_MSG_TYPE_JOIN_ROOM, -101, "用户或房间数据不存在");
        }
        String responseMsg = GameMessageJsonHelper.convertToJson(response);
        for (GameWebSocket item : webSocketSet) {
            item.sendMessage(responseMsg);
        }
        return null;
    }

    private void handleExitRoom(String username, long roomName) throws IOException {
        BaseGameResponse response = null;
        Room savedRoom = null;
        IUserService userService = applicationContext.getBean(UserService.class);
        IGameService gameService = applicationContext.getBean(GameService.class);
        User user = userService.getUserInfo(username);
        Room room = gameService.getRoomInfo(roomName);
        if (user != null && room != null) {
            Iterator<User> it = room.getPlayers().iterator();
            boolean singlePerson = room.getPlayers().size() == 1;
            while (it.hasNext()) {
                User u = it.next();
                if (u != null && u.getUsername().equals(username)) {
                    it.remove();
                    if (!singlePerson && u.getUsername().equals(room.getOwnerName())) {
                        room.setOwnerName(room.getPlayers().get(0).getUsername());
                    }
                    savedRoom = gameService.saveRoom(room);
                    response = BaseGameResponse.good(Config.GAME_MSG_TYPE_EXIT_ROOM, savedRoom);
                    break;
                }
            }
            if (response == null)
                response = BaseGameResponse.bad(Config.GAME_MSG_TYPE_EXIT_ROOM, -102, "用户不在该房间中");
        } else {
            response = BaseGameResponse.bad(Config.GAME_MSG_TYPE_EXIT_ROOM, -101, "用户或房间数据不存在");
        }
        String responseMsg = GameMessageJsonHelper.convertToJson(response);
        for (GameWebSocket item : webSocketSet) {
            item.sendMessage(responseMsg);
        }
        if (savedRoom != null && savedRoom.getPlayers().size() == 0)
            gameService.destroyRoom(roomName);
    }

    private BaseGameResponse handleReady(String username, long roomName) throws IOException {
        BaseGameResponse response = null;
        boolean isInRoom = false;
        IUserService userService = applicationContext.getBean(UserService.class);
        IGameService gameService = applicationContext.getBean(GameService.class);
        User user = userService.getUserInfo(username);
        Room room = gameService.getRoomInfo(roomName);
        if (user != null && room != null) {
            if (username.equals(room.getOwnerName())) {
                response = BaseGameResponse.bad(Config.GAME_MSG_TYPE_READY, -105, "房主无法准备");
            } else {
                for (User u : room.getPlayers()) {
                    if (u != null && u.getUsername().equals(username)) {
                        isInRoom = true;
                        break;
                    }
                }
                if (!isInRoom) {
                    return BaseGameResponse.bad(Config.GAME_MSG_TYPE_READY, -102, "用户不在该房间中");
                } else {
                    room.removePlayer(user);
                    user.setReady(true);
                    room.addPlayer(user);
                    Room savedRoom = gameService.saveRoom(room);
                    List<String> readyUsers = new ArrayList<>();
                    for (User u : room.getPlayers()) {
                        if (u.isReady()) {
                            readyUsers.add(u.getUsername());
                        }
                    }
                    response = BaseGameResponse.good(Config.GAME_MSG_TYPE_READY, readyUsers);
                }
            }
        } else {
            return BaseGameResponse.bad(Config.GAME_MSG_TYPE_READY, -101, "用户或房间数据不存在");
        }
        String responseMsg = GameMessageJsonHelper.convertToJson(response);
        for (GameWebSocket item : webSocketSet) {
            item.sendMessage(responseMsg);
        }
        return null;
    }

    private BaseGameResponse handleStartGame(String username, long roomName) throws IOException {
        BaseGameResponse response = null;
        boolean isAllReady = true;
        IUserService userService = applicationContext.getBean(UserService.class);
        IGameService gameService = applicationContext.getBean(GameService.class);
        User user = userService.getUserInfo(username);
        Room room = gameService.getRoomInfo(roomName);
        if (user != null && room != null) {
            for (User u : room.getPlayers()) {
                if (!u.getUsername().equals(room.getOwnerName()) && !u.isReady()) {
                    isAllReady = false;
                }
            }
            if (!isAllReady) {
                return BaseGameResponse.bad(Config.GAME_MSG_TYPE_START_GAME, -106, "玩家没有全部准备");
            } else {
                for (User u : room.getPlayers()) {
                    u.setReady(false);
                    userService.updateAndSaveUser(u);
                }
                response = BaseGameResponse.good(Config.GAME_MSG_TYPE_START_GAME, "");
            }
        } else {
            return BaseGameResponse.bad(Config.GAME_MSG_TYPE_START_GAME, -101, "用户或房间数据不存在");
        }
        String responseMsg = GameMessageJsonHelper.convertToJson(response);
        for (GameWebSocket item : webSocketSet) {
            item.sendMessage(responseMsg);
        }
        return null;
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


    private void setUsernameAndRoom(String username, long room) {
        this.username = username;
        this.roomName = room;
    }
}
