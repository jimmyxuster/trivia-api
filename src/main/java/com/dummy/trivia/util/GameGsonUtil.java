package com.dummy.trivia.util;

import com.google.gson.Gson;


public class GameGsonUtil {

    private static Gson gson = new Gson();

    public static String convertToJson(Object obj) {
        if (obj == null) {
            return "";
        }
        return gson.toJson(obj);
    }
}
