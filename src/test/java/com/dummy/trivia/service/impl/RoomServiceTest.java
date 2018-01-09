package com.dummy.trivia.service.impl;

import com.dummy.trivia.db.model.Room;
import com.dummy.trivia.db.model.SequenceId;
import com.dummy.trivia.db.model.base.BaseGameResponse;
import com.dummy.trivia.db.repository.RoomRepository;
import com.dummy.trivia.db.repository.UserRepository;
import com.dummy.trivia.service.IGameService;
import com.dummy.trivia.service.IUserService;
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

import javax.transaction.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class RoomServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    WebApplicationContext applicationContext;

    @Autowired
    MongoTemplate mongoTemplate;

    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    private Long getNextRoomId(String collName) {
        Query query = new Query(Criteria.where("collName").is(collName));
        Update update = new Update();
        update.inc("sequenceId", 1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(true);
        options.returnNew(true);
        SequenceId seqId = mongoTemplate.findAndModify(query, update, options, SequenceId.class);
        return seqId.getSequenceId();
    }

    @Before
    public void initialize() {
        GameWebSocket.setApplicationContext(applicationContext);
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void createRoom_roomShouldBeCorrectlyCreated() throws Exception {
        Room room = new Room();
        room.setQuestionType("Literature");
        Gson gson = new Gson();
        String roomJson = gson.toJson(room);
        String responseString = mockMvc.perform(MockMvcRequestBuilders
                                .post("/game/room")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(roomJson))
                                .andExpect(MockMvcResultMatchers.status().isOk())    //返回的状态是200
                                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println(responseString);
        assertEquals(responseString, "{\"code\":0,\"result\":{\"roomName\":" + (getNextRoomId("Room") - 1) + ",\"ownerName\":\"senia\",\"players\":[{\"username\":\"senia\",\"avatarUrl\":null,\"winCount\":3,\"totalPlay\":4,\"exp\":35,\"isReady\":false}],\"status\":\"Avail\",\"questionType\":\"Literature\"},\"message\":\"\"}");
    }

    @Test
    public void createRoom_roomShouldNotBeCreatedBecauseOfEmptyQuestionType() throws Exception {
        Room room = new Room();
        Gson gson = new Gson();
        String roomJson = gson.toJson(room);
        String responseString = mockMvc.perform(MockMvcRequestBuilders
                .post("/game/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(roomJson))
                .andExpect(MockMvcResultMatchers.status().isOk())    //返回的状态是200
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println(responseString);
        assertEquals(responseString, "{\"code\":-10030,\"result\":null,\"message\":\"问题类型不存在\"}");
    }

    @Test
    public void joinRoom_joiningShouldBeSuccessful() throws Exception {
        Room room = new Room();
        room.setQuestionType("Literature");
        room.setOwnerName("test");
        room.addPlayer(userRepository.findByUsername("test"));
        Room savedRoom = roomRepository.save(room);
        GameWebSocket socket = new GameWebSocket();
        BaseGameResponse response = socket.handleJoinRoom("senia", savedRoom.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "");
    }

    @Test
    public void joinRoom_joiningShouldBeFailedBecauseOfMistakenUsername() throws Exception {
        Room room = new Room();
        room.setQuestionType("Literature");
        room.setOwnerName("test");
        room.addPlayer(userRepository.findByUsername("test"));
        Room savedRoom = roomRepository.save(room);
        GameWebSocket socket = new GameWebSocket();
        BaseGameResponse response = socket.handleJoinRoom("unknown", savedRoom.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "{\"type\":\"joinRoom\",\"code\":-101,\"body\":null,\"message\":\"用户或房间数据不存在\"}");
    }

    @Test
    public void joinRoom_joiningShouldBeFailedBecauseOfMistakenRoomName() throws Exception {
        Room room = roomRepository.findByRoomName("unknown");
        GameWebSocket socket = new GameWebSocket();
        if (room == null) {
            room = new Room();
        }
        BaseGameResponse response = socket.handleJoinRoom("senia", room.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "{\"type\":\"joinRoom\",\"code\":-101,\"body\":null,\"message\":\"用户或房间数据不存在\"}");
    }

    @Test
    public void joinRoom_joiningShouldBeFailedBecauseOfFullRoom() throws Exception {
        Room room = new Room();
        room.setQuestionType("Literature");
        room.setOwnerName("test");
        room.addPlayer(userRepository.findByUsername("test"));
        room.addPlayer(userRepository.findByUsername("billy"));
        room.addPlayer(userRepository.findByUsername("banana"));
        room.addPlayer(userRepository.findByUsername("van"));
        Room savedRoom = roomRepository.save(room);
        GameWebSocket socket = new GameWebSocket();
        BaseGameResponse response = socket.handleJoinRoom("senia", savedRoom.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "{\"type\":\"joinRoom\",\"code\":-104,\"body\":null,\"message\":\"房间已满\"}");
    }

    @Test
    public void joinRoom_joiningShouldBeFailedBecauseOfUserAlreadyExists() throws Exception {
        Room room = new Room();
        room.setQuestionType("Literature");
        room.setOwnerName("test");
        room.addPlayer(userRepository.findByUsername("test"));
        room.addPlayer(userRepository.findByUsername("senia"));
        Room savedRoom = roomRepository.save(room);
        GameWebSocket socket = new GameWebSocket();
        BaseGameResponse response = socket.handleJoinRoom("senia", savedRoom.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "{\"type\":\"joinRoom\",\"code\":-103,\"body\":null,\"message\":\"玩家已经在房间内\"}");
    }

    @Test
    public void exitRoom_exitingShouldBeSuccessful() throws Exception {
        Room room = new Room();
        room.setQuestionType("Literature");
        room.setOwnerName("test");
        room.addPlayer(userRepository.findByUsername("test"));
        room.addPlayer(userRepository.findByUsername("senia"));
        Room savedRoom = roomRepository.save(room);
        GameWebSocket socket = new GameWebSocket();
        BaseGameResponse response = socket.handleExitRoom("senia", savedRoom.getRoomName());
        assertNull(response);
    }

    @Test
    public void exitRoom_exitingShouldBeFailedBecauseOfMistakenUsername() throws Exception {
        Room room = new Room();
        room.setQuestionType("Literature");
        room.setOwnerName("test");
        room.addPlayer(userRepository.findByUsername("test"));
        room.addPlayer(userRepository.findByUsername("senia"));
        Room savedRoom = roomRepository.save(room);
        GameWebSocket socket = new GameWebSocket();
        BaseGameResponse response = socket.handleExitRoom("unknown", savedRoom.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "{\"type\":\"exitRoom\",\"code\":-101,\"body\":null,\"message\":\"用户或房间数据不存在\"}");
    }

    @Test
    public void exitRoom_exitingShouldBeFailedBecauseOfMistakenRoomname() throws Exception {
        Room room = roomRepository.findByRoomName("unknown");
        GameWebSocket socket = new GameWebSocket();
        if (room == null) {
            room = new Room();
        }
        BaseGameResponse response = socket.handleExitRoom("senia", room.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "{\"type\":\"exitRoom\",\"code\":-101,\"body\":null,\"message\":\"用户或房间数据不存在\"}");
    }

    @Test
    public void exitRoom_exitingShouldBeFailedBecauseOfUserNotInTheRoom() throws Exception {
        Room room = new Room();
        room.setQuestionType("Literature");
        room.setOwnerName("test");
        room.addPlayer(userRepository.findByUsername("test"));
        Room savedRoom = roomRepository.save(room);
        GameWebSocket socket = new GameWebSocket();
        BaseGameResponse response = socket.handleExitRoom("senia", savedRoom.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "{\"type\":\"exitRoom\",\"code\":-102,\"body\":null,\"message\":\"用户不在该房间中\"}");
    }

    @Test
    public void exitRoom_ownerShouldBeChangedBecauseOfRoomOwnerExits() throws Exception {
        Room room = new Room();
        room.setQuestionType("Literature");
        room.setOwnerName("senia");
        room.addPlayer(userRepository.findByUsername("test"));
        room.addPlayer(userRepository.findByUsername("senia"));
        Room savedRoom = roomRepository.save(room);
        GameWebSocket socket = new GameWebSocket();
        BaseGameResponse response = socket.handleExitRoom("senia", savedRoom.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "");
    }

    @Test
    public void exitRoom_roomShouldBeDestroyedBecauseOfLastUserExits() throws Exception {
        Room room = new Room();
        room.setQuestionType("Literature");
        room.setOwnerName("senia");
        room.addPlayer(userRepository.findByUsername("senia"));
        Room savedRoom = roomRepository.save(room);
        GameWebSocket socket = new GameWebSocket();
        BaseGameResponse response = socket.handleExitRoom("senia", savedRoom.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "");
    }

    @Test
    public void getReady_readyStatusShouldBeSetToTrueSuccessfully() throws Exception {
        Room room = new Room();
        room.setQuestionType("Literature");
        room.setOwnerName("test");
        room.addPlayer(userRepository.findByUsername("test"));
        room.addPlayer(userRepository.findByUsername("senia"));
        Room savedRoom = roomRepository.save(room);
        GameWebSocket socket = new GameWebSocket();
        BaseGameResponse response = socket.handleReady("senia", savedRoom.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "");
    }

    @Test
    public void getReady_readyShouldFailBecauseOfMistakenUsername() throws Exception {
        Room room = new Room();
        room.setQuestionType("Literature");
        room.setOwnerName("test");
        room.addPlayer(userRepository.findByUsername("test"));
        room.addPlayer(userRepository.findByUsername("senia"));
        Room savedRoom = roomRepository.save(room);
        GameWebSocket socket = new GameWebSocket();
        BaseGameResponse response = socket.handleReady("unknown", savedRoom.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "{\"type\":\"ready\",\"code\":-101,\"body\":null,\"message\":\"用户或房间数据不存在\"}");
    }

    @Test
    public void getReady_readyShouldFailBecauseOfMistakenRoomName() throws Exception {
        Room room = roomRepository.findByRoomName("unknown");
        GameWebSocket socket = new GameWebSocket();
        if (room == null) {
            room = new Room();
        }
        BaseGameResponse response = socket.handleReady("unknown", room.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "{\"type\":\"ready\",\"code\":-101,\"body\":null,\"message\":\"用户或房间数据不存在\"}");
    }

    @Test
    public void getReady_readyShouldFailBecauseOfUserNotInTheRoom() throws Exception {
        Room room = new Room();
        room.setQuestionType("Literature");
        room.setOwnerName("test");
        room.addPlayer(userRepository.findByUsername("test"));
        Room savedRoom = roomRepository.save(room);
        GameWebSocket socket = new GameWebSocket();
        BaseGameResponse response = socket.handleReady("senia", savedRoom.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "{\"type\":\"ready\",\"code\":-102,\"body\":null,\"message\":\"用户不在该房间中\"}");
    }

    @Test
    public void getReady_readyShouldFailBecauseOfUserIsRoomOwner() throws Exception {
        Room room = new Room();
        room.setQuestionType("Literature");
        room.setOwnerName("senia");
        room.addPlayer(userRepository.findByUsername("test"));
        room.addPlayer(userRepository.findByUsername("senia"));
        Room savedRoom = roomRepository.save(room);
        GameWebSocket socket = new GameWebSocket();
        BaseGameResponse response = socket.handleReady("senia", savedRoom.getRoomName());
        String responseJson = GameMessageJsonHelper.convertToJson(response);
        assertEquals(responseJson, "{\"type\":\"ready\",\"code\":-105,\"body\":null,\"message\":\"房主无法准备\"}");
    }

    @After
    public void finish() {
    }




}
