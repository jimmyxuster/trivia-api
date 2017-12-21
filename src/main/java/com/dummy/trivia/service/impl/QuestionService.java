package com.dummy.trivia.service.impl;

import com.dummy.trivia.db.model.Player;
import com.dummy.trivia.db.model.Question;
import com.dummy.trivia.db.model.QuestionType;
import com.dummy.trivia.db.repository.QuestionRepository;
import com.dummy.trivia.service.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class QuestionService implements IQuestionService {

    @Autowired
    QuestionRepository questionRepository;

    @Override
    public void showQuestionAndChoices(long id) {

    }

    @Override
    public void showAnswer(Question question) {

    }

    @Override
    public boolean correctlyAnswered(Player player, Question question) {
        return false;
    }

    @Override
    public void getQuestionsOfType(QuestionType type) {

    }

    @Override
    public Question getRandomQuestion(List<Question> questions) {
        return null;
    }
}
