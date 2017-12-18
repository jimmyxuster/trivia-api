package com.dummy.trivia.service.impl;

import com.dummy.trivia.db.model.User;
import com.dummy.trivia.db.repository.UserRepository;
import com.dummy.trivia.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements IUserService {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder(4);

    @Autowired
    UserRepository userRepository;

    @Override
    public User getUserInfo(String username) {
        return StringUtils.isEmpty(username) ? null : userRepository.findByUsername(username);
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(ENCODER.encode(user.getPassword()));
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
        return userRepository.save(user);
    }
}
