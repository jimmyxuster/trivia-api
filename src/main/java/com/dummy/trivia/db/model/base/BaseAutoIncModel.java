package com.dummy.trivia.db.model.base;

import com.dummy.trivia.db.model.GeneratedValue;
import org.springframework.data.annotation.Id;

public class BaseAutoIncModel {

    @GeneratedValue
    @Id
    private long id;
}
