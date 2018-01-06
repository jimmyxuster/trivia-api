package com.dummy.trivia.db.model;

import com.google.gson.annotations.Expose;

public class ChatItem {
    @Expose
    private String username;
    @Expose
    private String content;

    public ChatItem(String username, String content) {
        this.username = username;
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
