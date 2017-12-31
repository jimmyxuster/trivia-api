package com.dummy.trivia.db.repository;

import com.dummy.trivia.db.model.Question;
import com.dummy.trivia.db.model.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {

    public Question findById(String id);

    public List<Question> findByType(String type);

}
