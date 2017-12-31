package com.dummy.trivia.service.impl;

import com.dummy.trivia.db.model.Player;
import com.dummy.trivia.db.model.User;
import com.dummy.trivia.db.repository.UserRepository;
import com.dummy.trivia.service.IUserService;
import com.dummy.trivia.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements IUserService {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder(4);
    public static final String AVATAR_ROOT = "avatar-dir";

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
        user.setWinCount(0);
        user.setTotalPlay(0);
        user.setExp(0);
        User savedUser = userRepository.save(user);
        if (savedUser != null) {
            if (!StringUtils.isEmpty(user.getAvatarBase64())) {
                try {
                    FileUtil.base64ToFile(user.getAvatarBase64(), Paths.get(AVATAR_ROOT).toString(), user.getId());
                } catch (IOException e) {
                    e.printStackTrace();
                    userRepository.delete(savedUser);
                    savedUser = null;
                }
            }
        }
        return savedUser;
    }

    @Override
    public int getLevelByExp(int exp) {
        if (exp < 0)
            return 0;
        else if (exp <= 10)
            return 1;
        else if (exp <= 30)
            return 2;
        else if (exp <= 60)
            return 3;
        else if (exp <= 100)
            return 4;
        else
            return 5;
    }
}
