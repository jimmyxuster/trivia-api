package com.dummy.trivia.service;

import com.dummy.trivia.db.model.ChangePasswordBean;
import com.dummy.trivia.db.model.User;

public interface IUserService {

    User getUserInfo(String username);

    User saveUser(User user);

    User updateAndSaveUser(User user);

//    List<User> getReadyUsers();

    int getLevelByExp(int exp);

    User changePassword(User user, ChangePasswordBean bean);
}
