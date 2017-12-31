package com.dummy.trivia.db.model.type;

import com.dummy.trivia.db.model.base.BaseGameMessage;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

public class GameMessageJsonHelper {

    private static Gson gson;
    static {
        gson = new Gson();
    }
    public static <T> BaseGameMessage<T> fromJsonObject(String json, Class<T> clazz) {
        Type type = new ParameterizedTypeImpl(BaseGameMessage.class, new Class[]{clazz});
        return gson.fromJson(json, type);
    }
    public static <T> BaseGameMessage<List<T>> fromJsonArray(String json, Class<T> clazz) {
        // 生成List<T> 中的 List<T>
        Type listType = new ParameterizedTypeImpl(List.class, new Class[]{clazz});
        // 根据List<T>生成完整的Result<List<T>>
        Type type = new ParameterizedTypeImpl(BaseGameMessage.class, new Type[]{listType});
        return gson.fromJson(json, type);
    }
}
