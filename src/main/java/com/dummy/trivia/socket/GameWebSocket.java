package com.dummy.trivia.socket;

import com.dummy.trivia.config.Config;
import com.dummy.trivia.db.model.*;
import com.dummy.trivia.db.model.base.BaseGameResponse;
import com.dummy.trivia.db.model.game.Answer;
import com.dummy.trivia.db.model.game.TakeTurn;
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

    private static int onlineCount = 0;
    private static CopyOnWriteArraySet<GameWebSocket> webSocketSet = new CopyOnWriteArraySet<>();
    private Session session;

    private String username;
    private long roomName;

    private Game game;
    private Question onGoingQuestion;
    private Player onGoingPlayer;

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
        System.out.println("有新用户加入!当前在线人数为" + getOnlineCount());
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

    @OnMessage
    public String onMessage(String message) throws IOException {
        JsonObject json = GameMessageJsonHelper.parse(message);

        String type = json.get("type").getAsString();
        switch (type) {
            case "joinRoom":
                String joinUsername = json.get("username").getAsString();
                long joinRoomName = json.get("roomName").getAsLong();
                setUsernameAndRoom(joinUsername, joinRoomName);
                BaseGameResponse joinResponse = handleJoinRoom(joinUsername, joinRoomName);
                if (joinResponse != null) {
                    return GameMessageJsonHelper.convertToJson(joinResponse);
                }
                return null;
            case "exitRoom":
                String exitUsername = json.get("username").getAsString();
                long exitRoomName = json.get("roomName").getAsLong();
                BaseGameResponse exitResponse = handleExitRoom(exitUsername, exitRoomName);
                if (exitResponse != null) {
                    return GameMessageJsonHelper.convertToJson(exitResponse);
                }
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
                if (errorStartGame == null) {
                    IGameService gameService = applicationContext.getBean(GameService.class);
                    game = gameService.getGame(startGameRoomName);
                    return null;
                } else {
                    return GameMessageJsonHelper.convertToJson(errorStartGame);
                }
            case "takeTurn":
                BaseGameResponse takeTurnResponse = handleTakeTurn(game);
                return takeTurnResponse == null ? null : GameMessageJsonHelper.convertToJson(takeTurnResponse);
            case "answer":
                String choice = json.get("choice").getAsString();
                BaseGameResponse answerResponse = handleAnswer(choice);
                if (answerResponse != null) {
                    return GameMessageJsonHelper.convertToJson(answerResponse);
                }
                return null;
            case "gameOver":
                BaseGameResponse gameOverResponse = handleGameOver(game);
                return gameOverResponse == null ? null : GameMessageJsonHelper.convertToJson(gameOverResponse);
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

    private BaseGameResponse handleExitRoom(String username, long roomName) throws IOException {
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
                return BaseGameResponse.bad(Config.GAME_MSG_TYPE_EXIT_ROOM, -102, "用户不在该房间中");
        } else {
            return BaseGameResponse.bad(Config.GAME_MSG_TYPE_EXIT_ROOM, -101, "用户或房间数据不存在");
        }
        String responseMsg = GameMessageJsonHelper.convertToJson(response);
        for (GameWebSocket item : webSocketSet) {
            item.sendMessage(responseMsg);
        }
        if (savedRoom != null && savedRoom.getPlayers().size() == 0)
            gameService.destroyRoom(roomName);
        return null;
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
                return BaseGameResponse.bad(Config.GAME_MSG_TYPE_READY, -105, "房主无法准备");
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
            if (!username.equals(room.getOwnerName())) {
                return BaseGameResponse.bad(Config.GAME_MSG_TYPE_START_GAME, -110, "不是房主，无法开始游戏");
            }
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
                Game game = gameService.initializeGame(roomName);
                if (game == null) {
                    return BaseGameResponse.bad(Config.GAME_MSG_TYPE_START_GAME, -107, "游戏创建失败");
                }
                else {
                    response = BaseGameResponse.good(Config.GAME_MSG_TYPE_START_GAME, game);
                }
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

    private BaseGameResponse handleTakeTurn(Game game) throws IOException {
        System.out.println("===============处理回合=================");
        if (game == null) {
            System.out.println("游戏数据不存在");
            return BaseGameResponse.bad(Config.GAME_MSG_TYPE_TAKE_TURN, -108, "游戏数据不存在");
        } else {
            BaseGameResponse response = null;
            IQuestionService questionService = applicationContext.getBean(QuestionService.class);
            IGameService gameService = applicationContext.getBean(GameService.class);
            Player nextPlayer = null;
            String name = null;
            System.out.println("游戏里的玩家依次是：" + game.getPlayersOrder());
            for (Player p : game.getPlayersOrder()) {
                name = p.getUsername();
                if (!game.findPlayer(name).isHasTakenTurn()) {
                    nextPlayer = game.findPlayer(name);
                    break;
                }
            }
            if (nextPlayer == null) {
                name = game.getPlayersOrder().get(0).getUsername();
                nextPlayer = game.findPlayer(name);
                List<Player> players = game.getPlayers();
                List<Player> tempRemoveList = new ArrayList<>();
                List<Player> tempAddList = new ArrayList<>();
                for (Player p : players) {
                    if (!p.getUsername().equals(nextPlayer.getUsername())) {
                        tempRemoveList.add(p);
                        p.setHasTakenTurn(false);
                        tempAddList.add(p);
                    }
                }
                game.getPlayers().removeAll(tempRemoveList);
                game.getPlayers().addAll(tempAddList);
            }
            onGoingPlayer = nextPlayer;
            System.out.println("下一个玩家是：" + nextPlayer.getUsername());
            System.out.println("玩家所在位置：" + nextPlayer.getPosition());
            System.out.println("题库中的题目数：" + game.getQuestions().size());
            Question question = questionService.getRandomQuestion(game.getQuestions());
            System.out.println("抽中的题目是：" + question);
            onGoingQuestion = question;
            TakeTurn takeTurn = new TakeTurn(nextPlayer.getUsername(), nextPlayer.isPrisoned(), question);

            System.out.println("骰子结果是：" + takeTurn.getRollNum());
            game.removePlayer(nextPlayer);
            if (nextPlayer.isPrisoned()) {
                System.out.println("现在是禁闭状态！");
                nextPlayer.setPrisoned(false);
                if (takeTurn.getRollNum() == 2 || takeTurn.getRollNum() == 4 || takeTurn.getRollNum() == 6) {
                    System.out.println("结果是偶数，跳过回合，不能答题和前进！");
                    takeTurn.setQuestion(null);
                    onGoingQuestion = null;
                } else {
                    System.out.println("结果是奇数，解除禁闭，正常答题和前进！");
                    nextPlayer.moveForward(takeTurn.getRollNum());
                }
                System.out.println("禁闭状态已经解除，现在是：" + nextPlayer.isPrisoned());
            } else {
                nextPlayer.moveForward(takeTurn.getRollNum());
            }
            System.out.println("现在的位置是：" + nextPlayer.getPosition());

            nextPlayer.setHasTakenTurn(true);
            game.addPlayer(nextPlayer);
            gameService.saveGame(game);
            System.out.println("游戏信息已保存");

            response = BaseGameResponse.good(Config.GAME_MSG_TYPE_TAKE_TURN, takeTurn);
            String responseMsg = GameMessageJsonHelper.convertToJson(response);
            for (GameWebSocket item : webSocketSet) {
                item.sendMessage(responseMsg);
            }
            return null;
        }
    }

    private BaseGameResponse handleAnswer(String choice) throws IOException {
        System.out.println("=====================处理问题=====================");
        System.out.println("当前回答者：" + onGoingPlayer.getUsername());
        System.out.println("当前问题：" + onGoingQuestion);
        System.out.println("原金币：" + game.findPlayer(onGoingPlayer.getUsername()).getCoinCount());
        System.out.println("原禁闭状态：" + game.findPlayer(onGoingPlayer.getUsername()).isPrisoned());
        BaseGameResponse response;
        if (onGoingQuestion == null) {
            response = BaseGameResponse.good(Config.GAME_MSG_TYPE_ANSWER, "禁闭状态，无法答题");
        } else {
            IQuestionService questionService = applicationContext.getBean(QuestionService.class);
            IGameService gameService = applicationContext.getBean(GameService.class);
            Answer answer = questionService.attemptAnswer(onGoingQuestion, choice);
            if (answer == null) {
                return BaseGameResponse.bad(Config.GAME_MSG_TYPE_ANSWER, -100, "没有正在进行的题目或作答格式错误");
            } else {
                response = BaseGameResponse.good(Config.GAME_MSG_TYPE_ANSWER, answer);
                game.removePlayer(onGoingPlayer);
                if (answer.isCorrect()) {
                    System.out.println("回答正确！金币+1！");
                    onGoingPlayer.incrementCoinCount();
                    if (onGoingPlayer.getCoinCount() >= 6) {
                        System.out.println(onGoingPlayer.getUsername() + "有6个金币了！游戏结束了！");
                        game.setStatus("over");
                        game.setWinner(onGoingPlayer);
                    }
                } else {
                    System.out.println("回答错误！关禁闭！");
                    onGoingPlayer.setPrisoned(true);
                }
                game.addPlayer(onGoingPlayer);
                gameService.saveGame(game);
                System.out.println("现金币：" + game.findPlayer(onGoingPlayer.getUsername()).getCoinCount());
                System.out.println("现禁闭状态：" + game.findPlayer(onGoingPlayer.getUsername()).isPrisoned());
                System.out.println("游戏状态已保存");
            }
        }
        String responseMsg = GameMessageJsonHelper.convertToJson(response);
        for (GameWebSocket item : webSocketSet) {
            item.sendMessage(responseMsg);
        }
        return null;
    }

    private BaseGameResponse handleGameOver(Game game) throws IOException {
        BaseGameResponse response;
        IGameService gameService = applicationContext.getBean(GameService.class);
        if (!game.getStatus().equals("over") || game.getWinner() == null) {
            return BaseGameResponse.bad(Config.GAME_MSG_TYPE_GAME_OVER, -109, "游戏还未结束");
        } else {
            gameService.afterGame(game);
            response = BaseGameResponse.good(Config.GAME_MSG_TYPE_GAME_OVER, game);
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
