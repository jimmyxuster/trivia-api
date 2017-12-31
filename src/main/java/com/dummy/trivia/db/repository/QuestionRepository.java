package com.dummy.trivia.db.repository;

import com.dummy.trivia.db.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, String> {

    public Question findById(String id);

    public List<Question> findByType(String type);

}
