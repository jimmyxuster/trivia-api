package com.dummy.trivia.service.impl;

import com.dummy.trivia.db.model.*;
import com.dummy.trivia.db.repository.GameRepository;
import com.dummy.trivia.db.repository.QuestionRepository;
import com.dummy.trivia.db.repository.RoomRepository;
import com.dummy.trivia.db.repository.UserRepository;
import com.dummy.trivia.service.IGameService;
import com.dummy.trivia.service.IPlayerService;
import com.dummy.trivia.service.IQuestionService;
import com.dummy.trivia.service.IUserService;
import com.dummy.trivia.socket.GameWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService implements IGameService {

    @Autowired
    GameRepository gameRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    IUserService userService;
    @Autowired
    IPlayerService playerService;
    @Autowired
    IQuestionService questionService;

    @Override
    public List<Room> getRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Room getRoomInfo(String roomName) {
        return StringUtils.isEmpty(roomName) ? null : roomRepository.findByRoomName(roomName);
    }

    @Override
    public Room createRoom(String playerName, String type) {
        Room room = new Room();
        room.setOwnerName(playerName);
        room.setStatus("Avail");
        room.setQuestionType(type);
        addPlayerToRoom(playerName, room);
        Room savedRoom = roomRepository.save(room);
        if (savedRoom != null) {
            return savedRoom;
        }
        return null;
    }

    @Override
    public Room enterRoom(String playerName, Room room) {
        if (room.getPlayers().size() >= 4)
            return null;
        if (!addPlayerToRoom(playerName, room)) return null;
        Room savedRoom = roomRepository.save(room);
        if (savedRoom != null)
            return savedRoom;
        return null;
    }

    private boolean addPlayerToRoom(String playerName, Room room) {
        User user = userRepository.findByUsername(playerName);
        for (User u : room.getPlayers()) {
            if (u.getUsername().equals(playerName))
                return false;
        }
        room.addPlayer(user);
        return true;
    }

    //将玩家从房间中移除，若移除后房间内没有玩家，删除房间
    @Override
    public void quitRoom(String playerName, Room room) {
        User user = userRepository.findByUsername(playerName);
        room.removePlayer(user);
        roomRepository.save(room);
        if (room.getPlayers().size() <= 0)
            destroyRoom(room.getRoomName());
    }

    @Override
    public void destroyRoom(String roomName) {
        roomRepository.deleteByRoomName(roomName);
    }

    @Override
    public Game initializeGame(String roomName) {
        //通过房间号查找房间
        Room room = getRoomInfo(roomName);
        if (room != null) {
            //设置房间状态为"running"并保存
            room.setStatus("running");
            Room savedRoom = roomRepository.save(room);
            //players是存放房间内所有玩家的列表
            List<User> users = room.getPlayers();
            List<Player> players = new ArrayList<>();
            for (User u : users) {
                Player player = new Player(u);
                players.add(player);
            }

            //questions是房间类别下的所有题目列表
            List<Question> questions = questionRepository.findByType(room.getQuestionType());

            Game game = null;
            //如果game已经存在则查找，否则新建
            if (gameRepository.findByRoomName(roomName) == null) {
                game = new Game();
            } else {
                game = gameRepository.findByRoomName(roomName);
            }
            //设置游戏的状态为"playing"、设置玩家、题库、房间号，并保存
            game.setStatus("playing");
            game.setPlayers(players);
            game.setQuestions(questions);
            game.setRoomName(roomName);
            Game savedGame = gameRepository.save(game);
            if (savedGame != null)
                return savedGame;
        }
        return null;
    }

    //返回游戏是否满足结束条件
//    @Override
//    public boolean gameOver(Game game) {
//        if (game != null) {
//            //通过房间号查找房间，得到玩家列表
//            Room room = roomRepository.findByRoomName(game.getRoomName());
//            List<String> playerNames = room.getPlayers();
//            System.out.println("玩家有：" + playerNames.toString());
//            //如果有玩家金币数>=6，设置游戏状态为"over"，设置该玩家为赢家，保存游戏
//            for (String playerName : playerNames) {
//                Player player = new Player(userService.getUserInfo(playerName));
//                System.out.println("玩家" + player.getUsername() + "的金币数是" + player.getCoinCount());
//                if (player.getCoinCount() >= 6) {
//                    System.out.println("大于6，游戏结束了！");
//                    game.setStatus("over");
//                    game.setWinner(player);
//                    Game savedGame = gameRepository.save(game);
//                    System.out.println("Savedgame: " + savedGame.toString());
//                    return true;
//                }
//            }
//            return false;
//        }
//        return true;
//    }

    @Override
    public void startGame(Game game) {
        if (game != null) {
//            MyWebSocket socket = new MyWebSocket();
//            boolean gameOver = game.getStatus().equals("over");
            //初始化玩家和题目信息
            List<Player> players = getPlayers(game);
            System.out.println("玩家列表: " + players.toString());
            List<Question> originalQuestions = game.getQuestions();
            List<Question> questions = new ArrayList<>();
            questions.addAll(originalQuestions);
            System.out.println("题目共有: " + questions.size());

            while (!game.getStatus().equals("over")) {
                //每名玩家循环
                for (Player player : players) {
                    System.out.println("==========================\n当前玩家: " + player.getUsername());
                    //掷骰子
                    int diceNum = playerService.rollDice(player);
                    System.out.println("骰子点数是: " + diceNum);
                    //若在禁闭，结果为奇数则放出且正常答题，否则跳过回合
                    if (player.isPrisoned()) {
                        System.out.println("禁闭状态！");
                        player.setPrisoned(false);
                        if (diceNum == 2 || diceNum == 4 || diceNum == 6) {
                            System.out.println("结果是偶数，跳过回合！");
                            continue;
                        } else
                            System.out.println("结果是奇数，解除禁闭！");
                    }
                    //玩家在格子上前进
                    System.out.println("玩家当前位置: " + player.getPosition());
                    playerService.moveForward(player, diceNum);
                    System.out.println("玩家移动后位置: " + player.getPosition());
                    //如果本局游戏题目即将用尽，则把题库恢复到游戏开始时的状态
                    System.out.println("当前还有" + questions.size() + "道题目！");
                    if (questions.size() <= 0) {
                        System.out.println("题库已用尽！刷新题库！");
                        questions.addAll(originalQuestions);
                        System.out.println("已刷新，当前有" + questions.size() + "道题目！");
                    }
                    //随机从题库选出一道题目，并从本局游戏题库中移除
                    Question question = questionService.getRandomQuestion(questions);
                    System.out.println("这道题目是: " + question.toString());
                    questions.remove(question);
                    System.out.println("从题库中移除这道题目，还有" + questions.size() + "道题目！");
                    //message是玩家发送的答题消息，type应为"answer"
                    String message = "{\n" +
                            "  \"type\" : answer,\n" +
                            "  \"choice\" : A\n" +
                            "}";
                    //答对则加1禁闭，答错则关禁闭
                    if (answerCorrect(question, message)) {
                        System.out.println("回答正确！原金币数为" + player.getCoinCount());
                        player.incrementCoinCount();
                        System.out.println("增加1金币，现在金币数为" + player.getCoinCount());
                        if (player.getCoinCount() >= 6) {
                            System.out.println("金币大于等于6！游戏结束了！");
                            game.setStatus("over");
                            game.setWinner(player);
                            gameRepository.save(game);
                            break;
                        }
                    } else {
                        System.out.println("回答错误！关禁闭！");
                        player.setPrisoned(true);
                    }
                }
            }
            System.out.println("跳出了循环！");
        }
    }

    //返回所有玩家列表
    @Override
    public List<Player> getPlayers(Game game) {
        if (game != null) {
            Room room = roomRepository.findByRoomName(game.getRoomName());
            List<User> users = room.getPlayers();
            List<Player> players = new ArrayList<>();
            for (User u : users) {
                Player player = new Player(u);
                players.add(player);
            }
            return players;
        }
        return null;
    }

    @Override
    public boolean answerCorrect(Question question, String message) {
        GameWebSocket socket = new GameWebSocket();
        String choice = "";
        try {
            choice = socket.onMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return question.getAnswer().equals(choice);
    }

    //在游戏结束后执行
    @Override
    public void afterGame(Game game) {
        if (!game.getStatus().equals("over")) {
            return;
        }
        //所有参与的玩家的总局数+1，经验+5
        System.out.println("给所有玩家发福利！");
        for (Player player : game.getPlayers()) {
            System.out.println("=====================\n玩家：" + player.getUsername());
            User playerUser = userRepository.findByUsername(player.getUsername());
            System.out.println("原总局数：" + playerUser.getTotalPlay());
            System.out.println("原胜利数：" + playerUser.getWinCount());
            System.out.println("原经验值：" + playerUser.getExp());

            playerUser.incrementTotalPlay();
            playerUser.incrementExpBy(5);
            userRepository.save(playerUser);

            System.out.println("现总局数：" + playerUser.getTotalPlay());
            System.out.println("现胜利数：" + playerUser.getWinCount());
            System.out.println("现经验值：" + playerUser.getExp());
        }
        //winner的胜场+1，经验额外+5
        Player winner = game.getWinner();
        System.out.println("给赢家发福利！");
        if (winner != null) {
            System.out.println("======================\n赢家是：" + winner.getUsername() + "!!!");
            User winnerUser = userRepository.findByUsername(winner.getUsername());
            winnerUser.incrementWinCount();
            winnerUser.incrementExpBy(5);
            userRepository.save(winnerUser);
            System.out.println("现总局数：" + winnerUser.getTotalPlay());
            System.out.println("现胜利数：" + winnerUser.getWinCount());
            System.out.println("现经验值：" + winnerUser.getExp());
        }
    }

    @Override
    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }
}
