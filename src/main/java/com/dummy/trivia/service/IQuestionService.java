package com.dummy.trivia.service;

import com.dummy.trivia.db.model.Player;
import com.dummy.trivia.db.model.Question;
import com.dummy.trivia.db.model.QuestionType;

import java.util.List;

public interface IQuestionService {

    void showQuestionAndChoices(long id);

    void showAnswer(Question question);

    boolean correctlyAnswered(Player player, Question question);

    void getQuestionsOfType(QuestionType type);

    Question getRandomQuestion(List<Question> questions);
}
