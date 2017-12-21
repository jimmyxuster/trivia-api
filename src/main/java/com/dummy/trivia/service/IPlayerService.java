package com.dummy.trivia.service;

import com.dummy.trivia.db.model.Player;
import com.dummy.trivia.db.model.Question;

public interface IPlayerService {

    void initializePlayer(Player player);

    void answerQuestion(Player player, Question question);

    void moveForward(Player player, int step);

    void RollDice(Player player);

    void incrementCoinCount(Player player);

    void incrementWinCount(Player player);

//    void sendEmotion();
}
