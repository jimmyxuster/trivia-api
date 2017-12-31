package com.dummy.trivia.db.repository;

import com.dummy.trivia.db.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GameRepository extends MongoRepository<Game, String> {
    public Game findByRoomName(String roomName);
}
