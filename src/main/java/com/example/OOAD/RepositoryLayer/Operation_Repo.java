package com.example.OOAD.RepositoryLayer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.OOAD.Course;

@Repository
public interface Operation_Repo extends JpaRepository<Course, Integer> {

}
