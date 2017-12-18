package com.dummy.trivia.rest;

import com.dummy.trivia.db.model.User;
import com.dummy.trivia.rest.common.RestResponse;
import com.dummy.trivia.service.IUserService;
import com.dummy.trivia.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserController {

    @Autowired
    IUserService userService;

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/user/{username}", method = RequestMethod.GET)
    public RestResponse getUserInfo(@PathVariable String username, HttpServletRequest request) {
        User user = userService.getUserInfo(username);
        if (user == null) {
            return RestResponse.bad(-10010, "用户不存在");
        } else {
            return RestResponse.good(user);
        }
    }

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public RestResponse getUserInfo(HttpServletRequest request) {
        String currentUsername = AuthenticationUtil.getCurrentUserAuthentication(request).getName();
        User user = userService.getUserInfo(currentUsername);
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
        return RestResponse.good(userService.saveUser(user));
    }
}
