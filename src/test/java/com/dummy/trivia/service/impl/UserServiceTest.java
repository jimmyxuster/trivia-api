package com.dummy.trivia.service.impl;

import com.dummy.trivia.db.model.ChangePasswordBean;
import com.dummy.trivia.db.model.User;
import com.dummy.trivia.db.repository.UserRepository;
import com.dummy.trivia.service.IUserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IUserService userService;

    @Before
    public void createTestUser() {
        User user = new User();
        user.setUsername("testname");
        user.setPassword(passwordEncoder.encode("12345"));
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
        user.setWinCount(0);
        user.setTotalPlay(0);
        user.setExp(0);
        userRepository.save(user);
    }

    @Test
    public void getUserInfo_userNameShouldEqualsToTestname() throws Exception {
        User user = userRepository.findByUsername("testname");
        assertEquals(userService.getUserInfo("testname"), user);
    }

    @Test
    public void getUserInfo_queryResultShouldBeNull() throws Exception {
        assertNull(userService.getUserInfo("unknown"));
    }

    @Test
    public void changePassword_changedUserPasswordShouldBeEncrypted123456() throws Exception {
        ChangePasswordBean bean = new ChangePasswordBean();
        bean.setNewPassword("123456");
        bean.setOldPassword("12345");
        User user = userRepository.findByUsername("testname");
        User changedUser = userService.changePassword(user, bean);
        assertEquals(bean.getNewPassword(), "123456");
    }

    @Test
    public void changePassword_returnValueShouldBeNullBecauseOfMistakenOldPassword() throws Exception {
        ChangePasswordBean bean = new ChangePasswordBean();
        bean.setNewPassword("123456");
        bean.setOldPassword("54321");
        User user = userRepository.findByUsername("testname");
        User changedUser = userService.changePassword(user, bean);
        assertNull(changedUser);
    }

    @Test
    public void changePassword_returnValueShouldBeNullBecauseOfEmptyNewPassword() throws Exception {
        ChangePasswordBean bean = new ChangePasswordBean();
        bean.setOldPassword("12345");
        User user = userRepository.findByUsername("testname");
        User changedUser = userService.changePassword(user, bean);
        assertNull(changedUser);
    }

    @Test
    public void register_savedUserShouldEqualsThatFindByUsername() throws Exception {
        User user = new User();
        user.setUsername("saveUserTest");
        user.setPassword("testpwd");
        User savedUser = userService.saveUser(user);
        assertEquals(savedUser, userRepository.findByUsername("saveUserTest"));
    }

    @Test
    public void register_savedUserShouldBeNullBecauseOfEmptyUsername() throws Exception {
        User user = new User();
        user.setPassword("testpwd");
        User savedUser = userService.saveUser(user);
        assertNull(savedUser);
    }

    @Test
    public void register_savedUserShouldBeNullBecauseOfEmptyPassword() throws Exception {
        User user = new User();
        user.setUsername("saveUserTest2");
        User savedUser = userService.saveUser(user);
        assertNull(savedUser);
    }

    @After
    public void destroyTestUser() {
        User user = userRepository.findByUsername("testname");
        if (user != null) {
            userRepository.delete(user);
        }
    }

}