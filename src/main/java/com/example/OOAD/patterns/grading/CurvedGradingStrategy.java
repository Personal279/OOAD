package com.example.OOAD.patterns.grading;

import org.springframework.stereotype.Component;

/**
 * Strategy Pattern — Concrete Strategy 3: Curved grading
 *
 * Applies a fixed curve (bonus points) before computing the letter grade.
 * Used by instructors who want to adjust difficulty after the fact.
 * Capped at 100 to avoid grades above maximum.
 */
@Component("curvedGrading")
public class CurvedGradingStrategy implements GradingStrategy {

    private static final double CURVE_BONUS = 10.0;

    @Override
    public String calculateGrade(double score) {
        double curvedScore = Math.min(score + CURVE_BONUS, 100.0);
        if (curvedScore >= 90) return "A";
        if (curvedScore >= 80) return "B";
        if (curvedScore >= 70) return "C";
        if (curvedScore >= 60) return "D";
        return "F";
    }
}
