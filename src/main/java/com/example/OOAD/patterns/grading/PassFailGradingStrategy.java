package com.example.OOAD.patterns.grading;

import org.springframework.stereotype.Component;

/**
 * Strategy Pattern — Concrete Strategy 2: Pass/Fail grading
 *
 * Used for quizzes that are evaluated purely on a pass/fail basis
 * (e.g. orientation quizzes, compliance tests).
 * Pass threshold is 50%.
 */
@Component("passFailGrading")
public class PassFailGradingStrategy implements GradingStrategy {

    private static final double PASS_THRESHOLD = 50.0;

    @Override
    public String calculateGrade(double score) {
        return score >= PASS_THRESHOLD ? "Pass" : "Fail";
    }
}
