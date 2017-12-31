package com.dummy.trivia.db.model;

import com.dummy.trivia.db.model.base.BaseModel;
import com.google.gson.annotations.Expose;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Room extends BaseModel {

    @Expose
    private String roomName;

    @Expose
    private String ownerName;

    @Expose
    private List<String> players;

    @Expose
    private String status;

    @Expose
    private String questionType;

    public Room() {
        this.roomName = getFourRandom();
        this.ownerName = null;
        this.players = new ArrayList<>();
        this.status = "Avail";
        this.questionType = null;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void addPlayer(String playerName) {
        this.players.add(playerName);
    }

    public void removePlayer(String playerName) {
        this.players.remove(playerName);
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

    public static String getFourRandom(){
        Random random = new Random();
        String fourRandom = random.nextInt(10000) + "";
        int randLength = fourRandom.length();
        if(randLength<4){
            for(int i=1; i<=4-randLength; i++)
                fourRandom = "0" + fourRandom;
        }
        return fourRandom;
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
