package com.dummy.trivia.rest;

import com.dummy.trivia.db.model.Game;
import com.dummy.trivia.db.model.Room;
import com.dummy.trivia.db.model.User;
import com.dummy.trivia.rest.common.RestResponse;
import com.dummy.trivia.service.IGameService;
import com.dummy.trivia.service.IUserService;
import com.dummy.trivia.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class GameController {

//    private Map<String, User> userMap = new HashMap<>();

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
    @GetMapping("/game/room")
    public RestResponse getRooms() {
        List<Room> rooms = gameService.getRooms();
        if (rooms == null) {
            return RestResponse.bad(-10011, "查找房间失败");
        } else {
            return RestResponse.good(rooms);
        }
    }

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/game/room", method = RequestMethod.POST)
    //创建房间，选择题目类型，并使当前用户成为房主，返回房间信息
    public RestResponse createRoom(@RequestBody Room room, HttpServletRequest request) {
        System.out.println(room.toString());
        String currentUserName = AuthenticationUtil.getCurrentUserAuthentication(request).getName();
        String type = room.getQuestionType();
        room = gameService.createRoom(currentUserName, type);
        if (room == null) {
            return RestResponse.bad(-10013, "创建房间失败");
        }
        room.setOwnerName(currentUserName);
        return RestResponse.good(room);
    }

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/game/room/{roomName}", method = RequestMethod.GET)
    //加入房间
    public RestResponse joinRoom(@PathVariable long roomName, HttpServletRequest request) {
        Room room = gameService.getRoomInfo(roomName);
        if (room == null) {
            return RestResponse.bad(-10014, "加入房间失败，房间不存在");
        }
        String currentUserName = AuthenticationUtil.getCurrentUserAuthentication(request).getName();
//        User user = userService.getUserInfo(currentUserName);
//        userMap.put(currentUserName, user);
        gameService.enterRoom(currentUserName, room);
        return RestResponse.good(room);
    }

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/game/room/{roomName}/quit", method = RequestMethod.GET)
    //加入房间
    public RestResponse quitRoom(@PathVariable long roomName, HttpServletRequest request) {
        Room room = gameService.getRoomInfo(roomName);
        if (room == null) {
            return RestResponse.bad(-10014, "退出房间失败，房间不存在");
        }
        String currentUserName = AuthenticationUtil.getCurrentUserAuthentication(request).getName();
//        User user = userService.getUserInfo(currentUserName);
//        userMap.put(currentUserName, user);
        gameService.quitRoom(currentUserName, room);
        return RestResponse.good(room);
    }

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/game/{roomName}", method = RequestMethod.GET)
    public RestResponse runGame(@PathVariable long roomName, Game game) {
        Room room = gameService.getRoomInfo(roomName);
        if (room == null) {
            return RestResponse.bad(-10014, "开始游戏失败，房间不存在");
        }
        game = gameService.initializeGame(roomName);
        System.out.println(game);
        gameService.startGame(game);
        gameService.afterGame(game);

        return RestResponse.good(game);
    }


}
