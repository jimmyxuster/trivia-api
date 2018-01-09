package com.dummy.trivia.service.impl;

import com.dummy.trivia.db.model.*;
import com.dummy.trivia.db.model.base.BaseGameResponse;
import com.dummy.trivia.db.model.game.Answer;
import com.dummy.trivia.db.model.game.TakeTurn;
import com.dummy.trivia.db.repository.QuestionRepository;
import com.dummy.trivia.db.repository.RoomRepository;
import com.dummy.trivia.db.repository.UserRepository;
import com.dummy.trivia.service.IGameService;
import com.dummy.trivia.service.IQuestionService;
import com.dummy.trivia.socket.GameWebSocket;
import com.dummy.trivia.util.GameMessageJsonHelper;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class GameServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private IGameService gameService;

    @Autowired
    WebApplicationContext applicationContext;

    @Autowired
    MongoTemplate mongoTemplate;

    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    @Before
    public void initialize() {
        GameWebSocket.setApplicationContext(applicationContext);
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void startGame_gameShouldBeSuccessfullyCreated() throws Exception {
        Room room = new Room();
        room.setQuestionType("Literature");
        room.setOwnerName("senia");
        User test = userRepository.findByUsername("test");
        test.setReady(true);
        User senia = userRepository.findByUsername("senia");
        room.addPlayer(test);
        room.addPlayer(senia);
        Room savedRoom = roomRepository.save(room);
        GameWebSocket socket = new GameWebSocket();
        BaseGameResponse response = socket.handleStartGame("senia", savedRoom.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "");
    }

    @Test
    public void startGame_gameCreatingShouldFailBecauseOfMistakenUsername() throws Exception {
        Room room = new Room();
        room.setQuestionType("Literature");
        room.setOwnerName("senia");
        User test = userRepository.findByUsername("test");
        test.setReady(true);
        User senia = userRepository.findByUsername("senia");
        room.addPlayer(test);
        room.addPlayer(senia);
        Room savedRoom = roomRepository.save(room);
        GameWebSocket socket = new GameWebSocket();
        BaseGameResponse response = socket.handleStartGame("unknown", savedRoom.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "{\"type\":\"startGame\",\"code\":-101,\"body\":null,\"message\":\"用户或房间数据不存在\"}");
    }

    @Test
    public void startGame_gameCreatingShouldFailBecauseOfMistakenRoomName() throws Exception {
        Room room = roomRepository.findByRoomName("unknown");
        GameWebSocket socket = new GameWebSocket();
        if (room == null) {
            room = new Room();
        }
        BaseGameResponse response = socket.handleStartGame("senia", room.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "{\"type\":\"startGame\",\"code\":-101,\"body\":null,\"message\":\"用户或房间数据不存在\"}");
    }

    @Test
    public void startGame_gameCreatingShouldFailBecauseOfUserNotInTheRoom() throws Exception {
        Room room = new Room();
        room.setQuestionType("Literature");
        room.setOwnerName("test");
        User test = userRepository.findByUsername("test");
        room.addPlayer(test);
        Room savedRoom = roomRepository.save(room);
        GameWebSocket socket = new GameWebSocket();
        BaseGameResponse response = socket.handleStartGame("senia", savedRoom.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "{\"type\":\"startGame\",\"code\":-110,\"body\":null,\"message\":\"不是房主，无法开始游戏\"}");
    }

    @Test
    public void startGame_gameCreatingShouldFailBecauseOfUserNotRoomOwner() throws Exception {
        Room room = new Room();
        room.setQuestionType("Literature");
        room.setOwnerName("test");
        User test = userRepository.findByUsername("test");
        User senia = userRepository.findByUsername("senia");
        senia.setReady(true);
        room.addPlayer(test);
        room.addPlayer(senia);
        Room savedRoom = roomRepository.save(room);
        GameWebSocket socket = new GameWebSocket();
        BaseGameResponse response = socket.handleStartGame("senia", savedRoom.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "{\"type\":\"startGame\",\"code\":-110,\"body\":null,\"message\":\"不是房主，无法开始游戏\"}");
    }

    @Test
    public void startGame_gameCreatingShouldFailBecauseOfOnlyOneUserInTheRoom() throws Exception {
        Room room = new Room();
        room.setQuestionType("Literature");
        room.setOwnerName("senia");
        User senia = userRepository.findByUsername("senia");
        senia.setReady(true);
        room.addPlayer(senia);
        Room savedRoom = roomRepository.save(room);
        GameWebSocket socket = new GameWebSocket();
        BaseGameResponse response = socket.handleStartGame("senia", savedRoom.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "{\"type\":\"startGame\",\"code\":-130,\"body\":null,\"message\":\"房间内只有一人，无法开始游戏\"}");
    }

    @Test
    public void startGame_gameCreatingShouldFailBecauseOfNotAllReady() throws Exception {
        Room room = new Room();
        room.setQuestionType("Literature");
        room.setOwnerName("senia");
        User test = userRepository.findByUsername("test");
        User senia = userRepository.findByUsername("senia");
        room.addPlayer(test);
        room.addPlayer(senia);
        Room savedRoom = roomRepository.save(room);
        GameWebSocket socket = new GameWebSocket();
        BaseGameResponse response = socket.handleStartGame("senia", savedRoom.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "{\"type\":\"startGame\",\"code\":-106,\"body\":null,\"message\":\"玩家没有全部准备\"}");
    }

    @Test
    public void rollDice_allGeneratedValueShouldBeIntegerBetween1And6() throws Exception {
        TakeTurn takeTurn = new TakeTurn();
        for (int i = 0; i < 1000 ; i++) {
            int rollNum = takeTurn.generateRandomInt(1, 6);
            assertTrue(rollNum >= 1 && rollNum <= 6);
        }
    }

    @Test
    public void playerMove_newPositionShouldBe16BecauseOfRollNum6() throws Exception {
        Player player = new Player();
        player.setPosition(10);
        player.moveForward(6);
        assertEquals(player.getPosition(), 10 + 6);
    }

    @Test
    public void playerMove_newPositionShouldBe3BecauseOfRollNum5() throws Exception {
        Player player = new Player();
        player.setPosition(19);
        player.moveForward(5);
        assertEquals(player.getPosition(), (19 + 5) % 22);
    }

    @Test
    public void takeTurn_allPlayersNamesShouldAppear10TimesInFixedOrder() throws Exception {
        List<Player> playersOrder = new ArrayList<>();
        Player test1 = new Player();
        Player test2 = new Player();
        Player test3 = new Player();
        playersOrder.add(test1);
        playersOrder.add(test2);
        playersOrder.add(test3);
        Game game = new Game();
        game.setPlayersOrder(playersOrder);
        List<Player> result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            result.addAll(game.getPlayersOrder());
        }
        assertEquals(result.size(), 30);
    }

    @Test
    public void gameOver_gameShouldSuccessfullyFinish() throws Exception {
        Game game = new Game();
        List<Player> players = new ArrayList<>();
        game.setPlayers(players);
        Player senia = new Player(userRepository.findByUsername("senia"));
        Player test = new Player(userRepository.findByUsername("test"));
        senia.setCoinCount(6);
        game.addPlayer(senia);
        game.addPlayer(test);
        game.setStatus("over");
        game.setWinner(senia);
        GameWebSocket socket = new GameWebSocket();
        BaseGameResponse response = socket.handleGameOver(game);
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "");
    }

    @Test
    public void gameOver_gameShouldNotFinishBecauseOfGameStatusIsNotOver() throws Exception {
        Game game = new Game();
        List<Player> players = new ArrayList<>();
        game.setPlayers(players);
        Player senia = new Player(userRepository.findByUsername("senia"));
        Player test = new Player(userRepository.findByUsername("test"));
        game.addPlayer(senia);
        game.addPlayer(test);
        game.setStatus("running");
        GameWebSocket socket = new GameWebSocket();
        BaseGameResponse response = socket.handleGameOver(game);
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "{\"type\":\"gameOver\",\"code\":-109,\"body\":null,\"message\":\"游戏还未结束\"}");
    }

    @Test
    public void updateUserInfo_testAndSeniaShouldHaveCorrectStatisticInfo() throws Exception {
        Game game = new Game();
        List<Player> players = new ArrayList<>();
        game.setPlayers(players);
        User seniaUser = userRepository.findByUsername("senia");
        User testUser = userRepository.findByUsername("test");
        int seniaWinCount = seniaUser.getWinCount();
        int testWinCount = testUser.getWinCount();
        int seniaTotalPlay = seniaUser.getTotalPlay();
        int testTotalPlay = testUser.getTotalPlay();
        Player senia = new Player(userRepository.findByUsername("senia"));
        Player test = new Player(userRepository.findByUsername("test"));
        senia.setCoinCount(6);
        game.addPlayer(senia);
        game.addPlayer(test);
        game.setStatus("over");
        game.setWinner(senia);
        gameService.afterGame(game);
        assertEquals(userRepository.findByUsername("senia").getWinCount(), seniaWinCount + 1);
        assertEquals(userRepository.findByUsername("senia").getTotalPlay(), seniaTotalPlay + 1);
        assertEquals(userRepository.findByUsername("test").getWinCount(), testWinCount);
        assertEquals(userRepository.findByUsername("test").getTotalPlay(), testTotalPlay + 1);
    }

    @After
    public void finish() {
    }




}
