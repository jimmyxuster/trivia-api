package com.dummy.trivia.service;

import com.dummy.trivia.db.model.Player;
import com.dummy.trivia.db.model.Question;

import java.util.List;

public interface IQuestionService {

    String getQuestionInfo(String id);

    boolean correctlyAnswered(Player player, Question question);

    List<Question> getQuestionsOfType(String type);

    Question getRandomQuestion(List<Question> questions);
}
