package com.dummy.trivia.db.model;

import com.dummy.trivia.db.model.base.BaseModel;
import com.google.gson.annotations.Expose;

import java.util.List;

public class Game extends BaseModel {

    @Expose
    private List<Player> players;
    @Expose
    private List<Question> questions;
    @Expose
    private String status;
    @Expose
    private Player winner;
    @Expose
    private String roomName;

    private int[] board;

    //棋盘格子总数
    public final static int BOARD_SIZE = 22;

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getPosition(Player player) {
        return player.getPosition();
    }

    public void setPosition(Player player, int position) {
        player.setPosition(position);
    }

    public int getCoinCount(Player player) {
        return player.getCoinCount();
    }

    public void setCoinCount(Player player, int count) {
        player.setCoinCount(count);
    }

    public boolean isPrisoned(Player player) {
        return player.isPrisoned();
    }

    public void sendIntoPrison(Player player) {
        player.setPrisoned(true);
    }

    public void releaseFromPrison(Player player) {
        player.setPrisoned(false);
    }

    @Override
    public String toString() {
        return "Game{" +
                "players=" + players +
                ", questions=" + questions +
                ", status='" + status + '\'' +
                ", winner=" + winner +
                ", roomName='" + roomName + '\'' +
                '}';
    }
}
