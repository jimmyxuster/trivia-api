package com.dummy.trivia.db.model.base;

import com.google.gson.annotations.Expose;

public class BaseGameMessage<T> {

    @Expose
    private String type;
    @Expose
    private T body;
    @Expose
    private String message;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
