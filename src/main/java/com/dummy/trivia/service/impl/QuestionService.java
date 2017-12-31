package com.dummy.trivia.service.impl;

import com.dummy.trivia.db.model.Player;
import com.dummy.trivia.db.model.Question;
import com.dummy.trivia.db.model.game.Answer;
import com.dummy.trivia.db.repository.QuestionRepository;
import com.dummy.trivia.service.IQuestionService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class QuestionService implements IQuestionService {

    @Autowired
    QuestionRepository questionRepository;

    private Question onGoingQuestion;

    @Override
    public String getQuestionInfo(String id) {
        Question question = questionRepository.findById(id);
        Gson gson = new Gson();
        return gson.toJson(question);
    }

    @Override
    public boolean correctlyAnswered(Player player, Question question) {
        return true;
    }

    @Override
    public List<Question> getQuestionsOfType(String type) {
        return questionRepository.findByType(type);
    }

    @Override
    public Question getRandomQuestion(List<Question> questions) {
        Collections.shuffle(questions);
        onGoingQuestion = questions.get(0);
        return onGoingQuestion;
    }

    @Override
    public Answer attemptAnswer(String answer) {
        if (onGoingQuestion == null) {
            return new Answer(answer, null);
        }
        return new Answer(answer, onGoingQuestion.getAnswer());
    }
}
