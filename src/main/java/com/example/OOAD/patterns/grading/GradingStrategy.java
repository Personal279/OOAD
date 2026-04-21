package com.example.OOAD.patterns.grading;

/**
 * Strategy Pattern (Behavioral) — GradingStrategy interface
 *
 * Intent: Define a familys of grading algorithms, encapsulate each one,
 * and make them interchangeable without changing the clients that use them.
 *
 * In this project: UserService uses a GradingStrategy to calculate a
 * letter grade from a numeric score. The strategy can be swapped at
 * runtime (e.g. percentage-based vs pass/fail vs curved grading)
 * without touching UserService or any controller.
 */
public interface GradingStrategy {

    /**
     * Calculate a letter grade from a numeric score.
     *
     * @param score the raw numeric score (e.g. 0–100)
     * @return a letter grade string such as "A", "B", "Pass", etc.
     */
    String calculateGrade(double score);
}
