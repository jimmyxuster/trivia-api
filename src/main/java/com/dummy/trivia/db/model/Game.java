package com.dummy.trivia.db.model;

import com.dummy.trivia.db.model.base.BaseModel;
import com.google.gson.annotations.Expose;

import java.util.List;

public class Game extends BaseModel {

    @Expose
    private List<Player> players;

    private List<Player> playersOrder;

    private List<Question> questions;
    @Expose
    private String status;
    @Expose
    private Player winner;
    @Expose
    private long roomName;
    @Expose
    private Question onGoingQuestion;
    @Expose
    private Player onGoingPlayer;

    private int[] board;

    //棋盘格子总数
    public final static int BOARD_SIZE = 22;

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Player findPlayer(String name) {
        for (Player p : this.getPlayers()) {
            if (p.getUsername().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public List<Player> getPlayersOrder() {
        return playersOrder;
    }

    public void setPlayersOrder(List<Player> playersOrder) {
        this.playersOrder = playersOrder;
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

    public long getRoomName() {
        return roomName;
    }

    public void setRoomName(long roomName) {
        this.roomName = roomName;
    }

    public Question getOnGoingQuestion() {
        return onGoingQuestion;
    }

    public void setOnGoingQuestion(Question onGoingQuestion) {
        this.onGoingQuestion = onGoingQuestion;
    }

    public Player getOnGoingPlayer() {
        return onGoingPlayer;
    }

    public void setOnGoingPlayer(Player onGoingPlayer) {
        this.onGoingPlayer = onGoingPlayer;
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
                ", playersOrder=" + playersOrder +
                ", status='" + status + '\'' +
                ", winner=" + winner +
                ", roomName=" + roomName +
                ", onGoingQuestion=" + onGoingQuestion +
                ", onGoingPlayer=" + onGoingPlayer +
                '}';
    }
}
