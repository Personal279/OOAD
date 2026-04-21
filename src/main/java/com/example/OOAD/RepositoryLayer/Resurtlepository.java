package com.example.OOAD.RepositoryLayer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.OOAD.Result;
import com.example.OOAD.dto.ResultDTO;

import java.util.List;

public interface Resurtlepository extends JpaRepository<Result, Integer> {

    @Query("SELECT new com.example.DBMS.dto.ResultDTO(" +
       "r.user.id, " +
       "r.quiz.quiz_id, " +
       "r.Score, " +
       "r.Feedback) " +
       "FROM Result r " +
       "WHERE r.user.id = :userId")
List<ResultDTO> findQuizResultsByUserId(@Param("userId") Integer userId);

}
