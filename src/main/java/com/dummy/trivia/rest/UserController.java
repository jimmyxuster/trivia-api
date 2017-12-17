package com.dummy.trivia.rest;

import com.dummy.trivia.db.model.User;
import com.dummy.trivia.db.repository.UserRepository;
import com.dummy.trivia.rest.common.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder(4);
    @Autowired
    UserRepository userRepository;

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/user/{username}", method = RequestMethod.GET)
    public RestResponse getUserInfo(@PathVariable String username, HttpServletRequest request) {
        /// 以下代码可获取当前登录用户名
//        SecurityContextImpl securityContextImpl = (SecurityContextImpl) request
//                .getSession().getAttribute("SPRING_SECURITY_CONTEXT");
//
//        System.out.println("Username:"
//                + securityContextImpl.getAuthentication().getName());
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return RestResponse.bad(-10010, "用户不存在");
        } else {
            return RestResponse.good(user);
        }
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public RestResponse save(@RequestBody User user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            return RestResponse.bad(-10010, "用户名或密码为空");
        }
        user.setPassword(ENCODER.encode(user.getPassword()));
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
        return RestResponse.good(userRepository.save(user));
    }
}
