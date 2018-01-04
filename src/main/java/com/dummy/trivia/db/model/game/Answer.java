package com.dummy.trivia.db.model.game;

import com.google.gson.annotations.Expose;

public class Answer {
    @Expose
    private String chosen;
    @Expose
    private String key;
    @Expose
    private boolean isCorrect;

    public Answer(String chosen, String key) {
        this.chosen = chosen;
        this.key = key;
        this.isCorrect = (chosen.equalsIgnoreCase(key));
    }

    public String getChosen() {
        return chosen;
    }

    public void setChosen(String chosen) {
        this.chosen = chosen;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public boolean isCorrectAnswered() {
        return this.key != null && this.key.equals(this.chosen);
    }
}
