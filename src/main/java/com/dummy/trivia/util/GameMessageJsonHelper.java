package com.dummy.trivia.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GameMessageJsonHelper {

    private static JsonParser parser;
    private static Gson gson;

    static {
        parser = new JsonParser();
        gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .serializeNulls().create();
    }

    public static JsonObject parse(String json) {
        return parser.parse(json).getAsJsonObject();
    }


    public static String convertToJson(Object obj) {
        if (obj == null) {
            return "";
        }
        return gson.toJson(obj);
    }
}
