package com.dummy.trivia.service.impl;

import com.dummy.trivia.db.model.*;
import com.dummy.trivia.db.repository.RoomRepository;
import com.dummy.trivia.db.repository.UserRepository;
import com.dummy.trivia.service.IPlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService implements IPlayerService {

    @Autowired
    RoomRepository roomRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public void createRoom(String playerName) {
        Room room = new Room();
        room.setOwnerName(playerName);
        User user = userRepository.findByUsername(playerName);
        room.addPlayer(user);
    }

    @Override
    public void joinRoom(String playerName, String roomName) {
        Room room = roomRepository.findByRoomName(roomName);
        if(room != null) {
            User user = userRepository.findByUsername(playerName);
            room.addPlayer(user);
        }
    }

    @Override
    public void initializePlayer(Player player) {

    }

    @Override
    public void answerQuestion(Player player, Question question) {

    }

    @Override
    public void moveForward(Player player, int step) {
        player.setPosition((player.getPosition() + step) % Game.BOARD_SIZE);
    }

    @Override
    public int rollDice(Player player) {
        return player.generateRandomInt(1, 6);
    }

    @Override
    public void incrementCoinCount(Player player) {
        player.incrementCoinCount();
    }
}
