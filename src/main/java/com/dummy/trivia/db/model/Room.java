package com.dummy.trivia.db.model;

import com.dummy.trivia.db.model.annotation.GeneratedValue;
import com.google.gson.annotations.Expose;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class Room {

    @GeneratedValue
    @Id
    @Expose
    private long roomName;

    @Expose
    private String ownerName;

    @Expose
    private List<User> players;

    @Expose
    private String status;

    @Expose
    private String questionType;

    public Room() {
        this.ownerName = null;
        this.players = new ArrayList<>();
        this.status = "Avail";
        this.questionType = null;
    }

    public long getRoomName() {
        return roomName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public List<User> getPlayers() {
        return players;
    }

    public void addPlayer(User user) {
        this.players.add(user);
    }

    public void removePlayer(User user) {
        this.players.remove(user);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomName='" + roomName + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", players=" + players +
                ", status='" + status + '\'' +
                ", questionType='" + questionType + '\'' +
                '}';
    }
}
