package com.dummy.trivia.service.impl;

import com.dummy.trivia.config.SpringUtil;
import com.dummy.trivia.db.model.User;
import com.dummy.trivia.db.repository.UserRepository;
import com.dummy.trivia.service.IUserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
//@WebAppConfiguration
//@ContextConfiguration(locations = "classpath:application-test.yml")
public class UserServiceTest {

    private static ApplicationContext applicationContext = com.dummy.trivia.SpringUtil.getApplicationContext();

    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    private PasswordEncoder bcryptPasswordEncoder;

    @Before
    public void createTestUser() {
        UserRepository userRepository = applicationContext.getBean(UserRepository.class);
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);
        User user = new User();
        user.setUsername(passwordEncoder.encode("testname"));
        user.setPassword("12345");
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
        user.setWinCount(0);
        user.setTotalPlay(0);
        user.setExp(0);
        userRepository.save(user);
    }

    @Test
    public void getUserInfo() throws Exception {
        IUserService userService = applicationContext.getBean(UserService.class);
        UserRepository userRepository = applicationContext.getBean(UserRepository.class);
        User user = userRepository.findByUsername("testname");
        assertEquals(userService.getUserInfo("testname"), user);
    }

    @Test
    public void changePassword() throws Exception {
    }

    @Test
    public void saveUser() throws Exception {
    }

    @Test
    public void updateAndSaveUser() throws Exception {
    }

    @After
    public void destroyTestUser() {
        UserRepository userRepository = applicationContext.getBean(UserRepository.class);
        User user = userRepository.findByUsername("testname");
        if (user != null) {
            userRepository.delete(user);
        }
    }

}