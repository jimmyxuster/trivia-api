package com.dummy.trivia.service;

import com.dummy.trivia.db.model.Player;
import com.dummy.trivia.db.model.Question;

public interface IPlayerService {

    void createRoom(String playerName);

    void joinRoom(String playerName, String id);

    void initializePlayer(Player player);

    void answerQuestion(Player player, Question question);

    void moveForward(Player player, int step);

    int rollDice(Player player);

    void incrementCoinCount(Player player);

    void incrementWinCount(Player player);

    int getLevelByExp(int exp);

//    void sendEmotion();
}
