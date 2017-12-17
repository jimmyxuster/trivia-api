package com.dummy.trivia.rest.common;

import com.google.gson.annotations.Expose;

public class RestResponse {

    @Expose
    private int code;

    @Expose
    private Object body;

    @Expose
    private String message;

    private RestResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private RestResponse(int code, Object body) {
        this.code = code;
        this.body = body;
        this.message = "";
    }

    public static RestResponse bad(int code, String message) {
        return new RestResponse(code, message);
    }

    public static RestResponse good(Object body) {
        return new RestResponse(0, body);
    }
}
