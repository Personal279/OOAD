package com.example.OOAD.patterns.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.example.OOAD.Quiz;
import com.example.OOAD.Result;
import com.example.OOAD.Users;
import com.example.OOAD.RepositoryLayer.QuizRepository;
import com.example.OOAD.RepositoryLayer.Resurtlepository;
import com.example.OOAD.RepositoryLayer.UserRepo;
import com.example.OOAD.dto.ResultDTO;
import com.example.OOAD.patterns.grading.GradingStrategy;
@Component
public class QuizFacade {

    private final QuizRepository quizRepository;

    private final GradingStrategy gradingStrategy;

    private final Resurtlepository resultRepository;

    private final UserRepo userRepo;

    @Autowired
    public QuizFacade(QuizRepository quizRepository,
                      @Qualifier("percentageGrading") GradingStrategy gradingStrategy,
                      Resurtlepository resultRepository,
                      UserRepo userRepo) {
        this.quizRepository   = quizRepository;
        this.gradingStrategy  = gradingStrategy;
        this.resultRepository = resultRepository;
        this.userRepo         = userRepo;
    }

    public ResultDTO submitQuiz(Integer userId, Integer quizId, Integer score) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found: " + quizId));

        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        int totalMarks = quiz.getTotal_marks();
        double percentage = totalMarks > 0 ? (score * 100.0) / totalMarks : 0;
        String grade = gradingStrategy.calculateGrade(percentage);

        String feedback = String.format(
                "Score: %d / %d (%.1f%%) — Grade: %s",
                score, totalMarks, percentage, grade
        );

        Result result = new Result();
        result.setUser(user);
        result.setQuiz(quiz);
        result.setScore(score);
        result.setFeedback(feedback);
        resultRepository.save(result);

        return new ResultDTO(userId, quizId, score, feedback);
    }
}