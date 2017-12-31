package com.dummy.trivia.service.impl;

import com.dummy.trivia.db.model.Player;
import com.dummy.trivia.db.model.Room;
import com.dummy.trivia.db.model.User;
import com.dummy.trivia.db.repository.GameRepository;
import com.dummy.trivia.db.repository.RoomRepository;
import com.dummy.trivia.db.repository.UserRepository;
import com.dummy.trivia.service.IGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class GameService implements IGameService {

    @Autowired
    GameRepository gameRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public Room getRoomInfo(String roomName) {
        return StringUtils.isEmpty(roomName) ? null : roomRepository.findByRoomName(roomName);
    }

    @Override
    public Room createRoom(String playerName) {
        User user = userRepository.findByUsername(playerName);
        Player player = new Player(user);
        Room room = new Room();
        room.setOwnerName(playerName);
        room.setStatus("Avail");
        Room savedRoom = roomRepository.save(room);
        if (savedRoom != null)
            return savedRoom;
        return null;
    }

    @Override
    public Room enterRoom(String playerName, Room room) {
        room.addPlayer(playerName);
        Room savedRoom = roomRepository.save(room);
        if (savedRoom != null)
            return savedRoom;
        return null;
    }

    @Override
    public void destroyRoom(String id) {
        roomRepository.delete(id);
    }



    //    @Override
//    public void startGame(Game game) {
//        game.addPlayer(new Player(new User()));
//        List<Question> questions = new ArrayList<>();
//        game.setQuestions(questions);
//
//    }
}
