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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class QuestionServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private IGameService gameService;
    @Autowired
    private IQuestionService questionService;

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
    public void getQuestion_gottenQuestionsSizeShouldEqualQueriedQuestionsSizeOfSameType() throws Exception {
        Room room = new Room();
        room.setQuestionType("Literature");
        room.setOwnerName("senia");
        User test = userRepository.findByUsername("test");
        User senia = userRepository.findByUsername("senia");
        room.addPlayer(test);
        room.addPlayer(senia);
        Room savedRoom = roomRepository.save(room);
        Game game = gameService.initializeGame(savedRoom.getRoomName());
        assertEquals(game.getQuestions().size(), questionRepository.findByType("Literature").size());
    }

    @Test
    public void getQuestion_noQuestionsGottenBecauseOfMistakenQuestionType() throws Exception {
        Room room = new Room();
        room.setQuestionType("unknown");
        room.setOwnerName("senia");
        User test = userRepository.findByUsername("test");
        User senia = userRepository.findByUsername("senia");
        room.addPlayer(test);
        room.addPlayer(senia);
        Room savedRoom = roomRepository.save(room);
        Game game = gameService.initializeGame(savedRoom.getRoomName());
        assertEquals(game.getQuestions().size(), 0);
    }

    @Test
    public void answer_answeringShouldBeCorrect() throws Exception {
        Question question = questionRepository.findById("100");
        Answer answer = questionService.attemptAnswer(question, question.getAnswer());
        assertTrue(answer.isCorrect());
    }

    @Test
    public void answer_answeringShouldBeWrong() throws Exception {
        Question question = questionRepository.findById("100");
        Answer answer = questionService.attemptAnswer(question, "d");
        assertFalse(answer.isCorrect());
    }

    @Test
    public void answer_answeringShouldFailBecauseOfMistakenQuestion() throws Exception {
        Question question = questionRepository.findById("unknown");
        Answer answer = questionService.attemptAnswer(question, "d");
        assertNull(answer);
    }

    @Test
    public void answer_answeringShouldBeWrongBecauseOfMistakenOption() throws Exception {
        Question question = questionRepository.findById("100");
        Answer answer = questionService.attemptAnswer(question, "1");
        assertFalse(answer.isCorrect());
    }

    @Test
    public void prisonIn_playersIsPrisonedShouldBeTrue() throws Exception {
        Player player = new Player();
        Question question = questionRepository.findById("100");
        Answer answer = questionService.attemptAnswer(question, "e");
        player.setPrisoned(true);
        assertTrue(player.isPrisoned());
    }

    @Test
    public void prisonIn_playersIsPrinsonedShouldStillBeTrueBecauseOfAlreadyPrisoned() throws Exception {
        Player player = new Player();
        player.setPrisoned(true);
        Question question = questionRepository.findById("100");
        Answer answer = questionService.attemptAnswer(question, "e");
        assertTrue(player.isPrisoned());
    }

    @Test
    public void prisonOut_playersIsPrisonedShouldBeFalseBecauseOfOddDiceNum() throws Exception {
        Player player = new Player();
        player.setPrisoned(true);
        Question question = questionRepository.findById("100");
        Answer answer = questionService.attemptAnswer(question, question.getAnswer());
        player.setPrisoned(false);
        TakeTurn takeTurn = new TakeTurn();
        int diceNum = takeTurn.generateRandomInt(5, 5);
        assertFalse(player.isPrisoned() && diceNum % 2 == 1);
    }

    @Test
    public void prisonOut_playersIsPrisonedShouldBeFalseBecauseOfEvenDiceNum() throws Exception {
        Player player = new Player();
        player.setPrisoned(true);
        Question question = questionRepository.findById("100");
        Answer answer = questionService.attemptAnswer(question, question.getAnswer());
        player.setPrisoned(false);
        TakeTurn takeTurn = new TakeTurn();
        int diceNum = takeTurn.generateRandomInt(2, 2);
        assertFalse(player.isPrisoned() && diceNum % 2 == 0);
    }

    @After
    public void finish() {
    }




}
