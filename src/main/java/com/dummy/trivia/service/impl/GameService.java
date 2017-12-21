package com.dummy.trivia.service.impl;

import com.dummy.trivia.db.repository.GameRepository;
import com.dummy.trivia.service.IGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService implements IGameService {

    @Autowired
    GameRepository gameRepository;

    @Override
    public void initializePlayers() {

    }

    //    @Override
//    public void startGame(Game game) {
//        game.addPlayer(new Player(new User()));
//        List<Question> questions = new ArrayList<>();
//        game.setQuestions(questions);
//
//    }
}
