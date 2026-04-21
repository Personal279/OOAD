package com.example.OOAD.patterns.grading;

import org.springframework.stereotype.Component;

/**
 * Strategy Pattern — Concrete Strategy 1: Percentage-based grading
 *
 * Standard A/B/C/D/F scale used as the default grading approach
 * in the e-learning platform.
 */
@Component("percentageGrading")
public class PercentageGradingStrategy implements GradingStrategy {

    @Override
    public String calculateGrade(double score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }
}
