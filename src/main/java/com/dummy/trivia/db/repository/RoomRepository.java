package com.dummy.trivia.db.repository;

import com.dummy.trivia.db.model.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomRepository extends MongoRepository<Room, Long> {

    public Room findByRoomName(String roomName);
    public void deleteByRoomName(String roomName);
}
