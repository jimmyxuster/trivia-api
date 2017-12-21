package com.dummy.trivia.service.impl;

import com.dummy.trivia.db.model.Player;
import com.dummy.trivia.db.model.Question;
import com.dummy.trivia.db.repository.PlayerRepository;
import com.dummy.trivia.service.IPlayerService;
import org.springframework.beans.factory.annotation.Autowired;

public class PlayerService implements IPlayerService {

    @Autowired
    PlayerRepository playerRepository;

    @Override
    public void initializePlayer(Player player) {

    }

    @Override
    public void answerQuestion(Player player, Question question) {

    }

    @Override
    public void moveForward(Player player, int step) {

    }

    @Override
    public void RollDice(Player player) {

    }

    @Override
    public void incrementCoinCount(Player player) {

    }

    @Override
    public void incrementWinCount(Player player) {

    }
}
