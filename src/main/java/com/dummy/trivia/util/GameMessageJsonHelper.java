package com.dummy.trivia.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GameMessageJsonHelper {

    private static JsonParser parser;
    static {
        parser = new JsonParser();
    }
    public static JsonObject parse(String json) {
        return parser.parse(json).getAsJsonObject();
    }
}
