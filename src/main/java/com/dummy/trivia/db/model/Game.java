package com.dummy.trivia.db.model;

import com.dummy.trivia.db.model.base.BaseModel;
import com.google.gson.annotations.Expose;

import java.util.List;

public class Game extends BaseModel {

    @Expose
    private List<Player> players;
    @Expose
    private List<Question> questions;

    private int[] board;

    //棋盘格子总数
    public final static int BOARD_SIZE = 30;

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
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

}
