package com.dummy.trivia.db.model.base;

import com.dummy.trivia.db.model.annotation.GeneratedValue;
import org.springframework.data.annotation.Id;

public class BaseAutoIncModel {

    @GeneratedValue
    @Id
    private long id;

    public long getId() {
        return id;
    }
}
