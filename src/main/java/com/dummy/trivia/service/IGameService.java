package com.dummy.trivia.service;

import com.dummy.trivia.db.model.Game;
import com.dummy.trivia.db.model.Player;
import com.dummy.trivia.db.model.Question;
import com.dummy.trivia.db.model.Room;

import java.util.List;

public interface IGameService {

    Room getRoomInfo(String id);

    Room createRoom(String playerName, String type);

    Room enterRoom(String playerName, Room room);

    void quitRoom(String playerName, Room room);

    void destroyRoom(String id);

    Game initializeGame(String roomName);

    boolean gameOver(Game game);

    void startGame(Game game);

    List<Player> getPlayers(Game game);

    boolean answerCorrect(Question question, String message);

    void afterGame(Game game);
//    void startGame(Game game);
}
