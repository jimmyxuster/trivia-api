package com.dummy.trivia.rest;

import com.dummy.trivia.service.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;

public class QuestionController {

    @Autowired
    IQuestionService questionService;


}
