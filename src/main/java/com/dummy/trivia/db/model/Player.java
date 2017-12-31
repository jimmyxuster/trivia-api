package com.dummy.trivia.db.model;

import com.google.gson.annotations.Expose;

import java.util.Random;

public class Player {

    @Expose
    private User user;

    @Expose
    private int coinCount = 0;

    @Expose
    private boolean isPrisoned = false;

    @Expose
    private int position = 0;

    public Player(User user) {
        this.user = user;
        this.coinCount = 0;
        this.isPrisoned = false;
        this.position = generateRandomInt(0, Game.BOARD_SIZE - 1);
    }

    public String getUsername() {
        return user.getUsername();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getCoinCount() {
        return coinCount;
    }

    public void setCoinCount(int coinCount) {
        this.coinCount = coinCount;
    }

    public void incrementCoinCount() {
        this.coinCount++;
    }

    public boolean isPrisoned() {
        return isPrisoned;
    }

    public void setPrisoned(boolean prisoned) {
        isPrisoned = prisoned;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int generateRandomInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    @Override
    public String toString() {
        return "Player{" +
                "user=" + user +
                ", coinCount=" + coinCount +
                ", isPrisoned=" + isPrisoned +
                ", position=" + position +
                '}';
    }
}
