package com.example.OOAD.RepositoryLayer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.OOAD.Question;

import java.util.List;

public interface QuestionsRepo extends JpaRepository<Question, Integer> {

    @Query("SELECT q FROM Question q JOIN FETCH q.options WHERE q.quiz.quiz_id = :quiz_id")
    List<Question> findByQuizIdWithOptions(@Param("quiz_id") int quizId);

}
