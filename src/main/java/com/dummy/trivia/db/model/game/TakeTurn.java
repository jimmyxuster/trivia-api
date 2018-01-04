package com.dummy.trivia.db.model.game;

import com.dummy.trivia.db.model.Question;
import com.google.gson.annotations.Expose;

import java.util.Random;

public class TakeTurn {
    @Expose
    private String next;
    @Expose
    private int rollNum;
    @Expose
    private boolean isPrisoned;
    @Expose
    private Question question;

    public TakeTurn(String nextName, boolean isPrisoned, Question question) {
        this.next = nextName;
        this.rollNum = generateRandomInt(1, 6);
        this.isPrisoned = isPrisoned;
        this.question = question;
    }

    private int generateRandomInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public int getRollNum() {
        return rollNum;
    }

    public void setRollNum(int rollNum) {
        this.rollNum = rollNum;
    }

    public boolean isPrisoned() {
        return isPrisoned;
    }

    public void setPrisoned(boolean prisoned) {
        isPrisoned = prisoned;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
