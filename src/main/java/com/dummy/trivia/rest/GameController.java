package com.dummy.trivia.rest;

import com.dummy.trivia.db.model.Room;
import com.dummy.trivia.db.model.User;
import com.dummy.trivia.rest.common.RestResponse;
import com.dummy.trivia.service.IGameService;
import com.dummy.trivia.service.IUserService;
import com.dummy.trivia.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class GameController {

    private Map<String, User> userMap = new HashMap<>();

    @Autowired
    IGameService gameService;
    @Autowired
    IUserService userService;

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public RestResponse getUserInfo(HttpServletRequest request) {
        String currentUsername = AuthenticationUtil.getCurrentUserAuthentication(request).getName();
        User user = userService.getUserInfo(currentUsername);
        if (currentUsername == null) {
            return RestResponse.bad(-10010, "用户不存在，无法进入游戏");
        } else {
            return RestResponse.good(user);
        }
    }

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/game/room", method = RequestMethod.POST)
    //创建房间，并使当前用户成为房主，返回房间信息
    public RestResponse createRoom(Room room, HttpServletRequest request) {
        String currentUserName = AuthenticationUtil.getCurrentUserAuthentication(request).getName();
        room = gameService.createRoom(currentUserName);
        if (room == null) {
            return RestResponse.bad(-10013, "创建房间失败");
        }
        room.setOwnerName(currentUserName);
        return RestResponse.good(room);
    }

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/game/room/{roomName}", method = RequestMethod.GET)
    public RestResponse joinRoom(@PathVariable String roomName, HttpServletRequest request) {
        Room room = gameService.getRoomInfo(roomName);
        if (room == null) {
            return RestResponse.bad(-10014, "加入房间失败，房间不存在");
        }
        String currentUserName = AuthenticationUtil.getCurrentUserAuthentication(request).getName();
        User user = userService.getUserInfo(currentUserName);
        userMap.put(currentUserName, user);
        gameService.enterRoom(currentUserName, room);
        return RestResponse.good(room);
    }

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/game/{room}", method = RequestMethod.GET)
    public RestResponse runGame(@PathVariable String roomId) {
        return RestResponse.bad(0, "");
    }


}
