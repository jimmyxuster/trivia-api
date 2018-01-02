package com.dummy.trivia.service;

import com.dummy.trivia.db.model.User;

import java.util.List;

public interface IUserService {

    User getUserInfo(String username);

    User saveUser(User user);

    User updateAndSaveUser(User user);

//    List<User> getReadyUsers();

    int getLevelByExp(int exp);
}
