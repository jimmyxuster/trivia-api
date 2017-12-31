package com.dummy.trivia.db.model.base;

import com.google.gson.annotations.Expose;

public class BaseGameResponse {
    @Expose
    private String type;
    @Expose
    private int code;
    @Expose
    private Object body;
    @Expose
    private String message;

    private BaseGameResponse(String type, int code, String message) {
        this.type = type;
        this.code = code;
        this.message = message;
    }

    private BaseGameResponse(String type, Object body) {
        this.type = type;
        this.code = 0;
        this.body = body;
        this.message = "";
    }

    public static BaseGameResponse bad(String type, int errCode, String message) {
        return new BaseGameResponse(type, errCode, message);
    }

    public static BaseGameResponse good(String type, Object result) {
        return new BaseGameResponse(type, result);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
