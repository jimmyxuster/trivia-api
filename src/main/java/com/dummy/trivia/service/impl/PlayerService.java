package com.dummy.trivia.service.impl;

import com.dummy.trivia.db.model.Game;
import com.dummy.trivia.db.model.Player;
import com.dummy.trivia.db.model.Question;
import com.dummy.trivia.db.model.Room;
import com.dummy.trivia.db.repository.PlayerRepository;
import com.dummy.trivia.db.repository.RoomRepository;
import com.dummy.trivia.service.IPlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService implements IPlayerService {

    @Autowired
    RoomRepository roomRepository;

    @Override
    public void createRoom(String playerName) {
        Room room = new Room();
        room.setOwnerName(playerName);
        room.addPlayer(playerName);
    }

    @Override
    public void joinRoom(String playerName, String roomName) {
        Room room = roomRepository.findByRoomName(roomName);
        if(room != null) {
            room.addPlayer(playerName);
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
