package com.dummy.trivia.rest;

import com.dummy.trivia.db.model.User;
import com.dummy.trivia.rest.common.RestResponse;
import com.dummy.trivia.service.IGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    @Autowired
    IGameService gameService;

}
