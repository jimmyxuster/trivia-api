package com.dummy.trivia.rest;

import com.dummy.trivia.db.model.ChangePasswordBean;
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
    public RestResponse getUserInfo(@PathVariable String username) {
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

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public RestResponse changePassword(@RequestBody ChangePasswordBean bean, HttpServletRequest request) {
        String currentUsername = AuthenticationUtil.getCurrentUserAuthentication(request).getName();
        User user = userService.getUserInfo(currentUsername);
        if (user == null) {
            return RestResponse.bad(-10010, "用户不存在");
        } else {
            User savedUser = userService.changePassword(user, bean);
            return savedUser == null ? RestResponse.bad(-10010, "密码错误") :
                    RestResponse.good(savedUser);
        }
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public RestResponse save(@RequestBody User user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            return RestResponse.bad(-10010, "用户名或密码为空");
        }
        if (userService.getUserInfo(user.getUsername()) != null) {
            return RestResponse.bad(-10011, "用户名已存在");
        }
        User savedUser = userService.saveUser(user);
        return savedUser == null ? RestResponse.bad(-10011, "保存用户信息失败") :
                RestResponse.good(savedUser);
    }
}
