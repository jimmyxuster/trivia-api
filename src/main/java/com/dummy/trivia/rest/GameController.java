package com.dummy.trivia.rest;

import com.dummy.trivia.service.IGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    @Autowired
    IGameService gameService;

}
