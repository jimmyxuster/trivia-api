package com.dummy.trivia.db.model;

import java.util.Random;

public class Player extends User {

    private User user;

    private int coinCount = 0;
    private boolean isPrisoned = false;
    private int position = 0;

    public Player(User user) {
        this.user = user;
        this.coinCount = 0;
        this.isPrisoned = false;
        this.position = generateRandomInt(0, Game.BOARD_SIZE - 1);
    }

    @Override
    public String getUsername() {
        return super.getUsername();
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
}
