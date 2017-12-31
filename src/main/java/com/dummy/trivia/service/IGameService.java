package com.dummy.trivia.service;

import com.dummy.trivia.db.model.Room;

public interface IGameService {

    Room getRoomInfo(String id);

    Room createRoom(String playerName);

    Room enterRoom(String playerName, Room room);

    void destroyRoom(String id);


//    void startGame(Game game);
}
