package com.example.OOAD.RepositoryLayer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.OOAD.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    
}
