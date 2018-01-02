package com.dummy.trivia.db.repository;

import com.dummy.trivia.db.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, Long> {
    User findByUsername(String username);
    List<User> findByIsReady(boolean isReady);
}
