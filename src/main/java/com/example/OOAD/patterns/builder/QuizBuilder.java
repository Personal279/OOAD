package com.example.OOAD.patterns.builder;

import java.util.ArrayList;
import java.util.List;

import com.example.OOAD.Course;
import com.example.OOAD.Option;
import com.example.OOAD.Question;
import com.example.OOAD.Quiz;

/**
 * Builder Pattern (Creational) — QuizBuilder
 *
 * Intent: Separate the construction of a complex object from its
 * representation so that the same construction process can create
 * different representations.
 *
 * In this project: A Quiz is a complex object — it has a title, type,
 * total marks, a linked Course, and a list of Questions each of which
 * has its own list of Options with correct/incorrect flags.
 * Previously all this assembly happened ad-hoc in the service layer or
 * was delegated entirely to the incoming JSON payload.
 *
 * The builder makes Quiz construction explicit, readable, and safe —
 * ensuring all required fields are set before the object is used.
 *
 * Usage example:
 * <pre>
 *   Quiz quiz = new QuizBuilder()
 *       .title("Java Basics Quiz")
 *       .type("MCQ")
 *       .course(course)
 *       .addQuestion("What is JVM?", 5,
 *           List.of("Runtime engine", "Compiler", "IDE", "OS"),
 *           "Runtime engine")
 *       .build();
 * </pre>
 */
public class QuizBuilder {

    private String title;
    private String type;
    private int totalMarks = 0;
    private Course course;
    private final List<Question> questions = new ArrayList<>();

    /** Set the quiz title. */
    public QuizBuilder title(String title) {
        this.title = title;
        return this;
    }

    /** Set the quiz type (e.g. "MCQ", "True/False"). */
    public QuizBuilder type(String type) {
        this.type = type;
        return this;
    }

    /** Link this quiz to an existing Course entity. */
    public QuizBuilder course(Course course) {
        this.course = course;
        return this;
    }

    /**
     * Add a question with its options to the quiz.
     * Total marks is auto-accumulated from each question's mark value.
     *
     * @param questionText the question body
     * @param marks        marks awarded for a correct answer
     * @param optionTexts  list of answer option strings
     * @param correctAnswer the text of the correct option (must match one in optionTexts)
     */
    public QuizBuilder addQuestion(String questionText, int marks,
                                    List<String> optionTexts,
                                    String correctAnswer) {
        Question question = new Question();
        question.setQuestionText(questionText);
        question.setMarks(marks);

        List<Option> options = new ArrayList<>();
        for (String text : optionTexts) {
            Option opt = new Option();
            opt.setOptionText(text);
            opt.setCorrect(text.equals(correctAnswer));
            opt.setQuestion(question);
            options.add(opt);
        }

        question.setOptions(options);
        question.setQuiz(null); // will be set when Quiz is assembled in build()

        questions.add(question);
        totalMarks += marks;
        return this;
    }

    /**
     * Construct and return the fully assembled Quiz.
     *
     * @throws IllegalStateException if title or course is missing
     */
    public Quiz build() {
        if (title == null || title.isBlank()) {
            throw new IllegalStateException("Quiz title must not be blank.");
        }
        if (course == null) {
            throw new IllegalStateException("Quiz must be linked to a Course.");
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setType(type);
        quiz.setTotal_marks(totalMarks);
        quiz.setCourse(course);

        // Wire back-references so JPA cascade works correctly
        for (Question q : questions) {
            q.setQuiz(quiz);
            for (Option o : q.getOptions()) {
                o.setQuestion(q);
            }
        }
        quiz.setQuestions(questions);

        return quiz;
    }
}
