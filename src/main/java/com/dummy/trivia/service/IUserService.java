package com.dummy.trivia.service;

import com.dummy.trivia.db.model.User;

public interface IUserService {

    User getUserInfo(String username);

    User saveUser(User user);

    int getLevelByExp(int exp);
}
