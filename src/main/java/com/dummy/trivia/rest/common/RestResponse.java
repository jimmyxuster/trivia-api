package com.dummy.trivia.rest.common;

import com.google.gson.annotations.Expose;

public class RestResponse {

    @Expose
    private int code;

    @Expose
    private Object result;

    @Expose
    private String message;

    private RestResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private RestResponse(int code, Object result) {
        this.code = code;
        this.result = result;
        this.message = "";
    }

    public static RestResponse bad(int code, String message) {
        return new RestResponse(code, message);
    }

    public static RestResponse good(Object result) {
        return new RestResponse(0, result);
    }
}
