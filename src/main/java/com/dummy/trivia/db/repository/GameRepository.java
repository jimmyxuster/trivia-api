package com.dummy.trivia.db.repository;

import com.dummy.trivia.db.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepository extends MongoRepository<Game, String> {
}
