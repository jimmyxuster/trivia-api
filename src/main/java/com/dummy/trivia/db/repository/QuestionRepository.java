package com.dummy.trivia.db.repository;

import com.dummy.trivia.db.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    public Question findById(String id);

    public List<Question> findByType(String type);

}
