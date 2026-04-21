package com.example.OOAD.ServiceLayer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.OOAD.Chat;
import com.example.OOAD.Course;
import com.example.OOAD.Enrollment;
import com.example.OOAD.Instructor;
import com.example.OOAD.Option;
import com.example.OOAD.Question;
import com.example.OOAD.Quiz;
import com.example.OOAD.Result;
import com.example.OOAD.Users;
import com.example.OOAD.RepositoryLayer.CourseRepository;
import com.example.OOAD.RepositoryLayer.DiscussionRepo;
import com.example.OOAD.RepositoryLayer.EnrollmentRepo;
import com.example.OOAD.RepositoryLayer.InstructorRepo;
import com.example.OOAD.RepositoryLayer.Operation_Repo;
import com.example.OOAD.RepositoryLayer.QuestionsRepo;
import com.example.OOAD.RepositoryLayer.QuizRepository;
import com.example.OOAD.RepositoryLayer.Resurtlepository;
import com.example.OOAD.RepositoryLayer.UserRepo;
import com.example.OOAD.dto.AuthDTO;
import com.example.OOAD.dto.DiscussionDTO;
import com.example.OOAD.dto.QuestionsDTO;
import com.example.OOAD.dto.QuizDTO;
import com.example.OOAD.dto.ResultDTO;
import com.example.OOAD.patterns.factory.UserFactory;
import com.example.OOAD.patterns.grading.GradingStrategy;

import jakarta.transaction.Transactional;

/**
 * UserService — Facade Pattern (Structural)
 *
 * Acts as a single unified entry point for all business operations in the
 * e-learning platform. Controllers never access repositories directly;
 * they always go through this service, hiding the complexity of multiple
 * repositories and domain rules behind a simple interface.
 *
 * Also uses:
 *  - Strategy Pattern (Behavioral): GradingStrategy for grade calculation
 *  - Factory Pattern (Creational):  UserFactory for user/instructor creation
 */
@Service
public class UserService {
    @Autowired
    private Resurtlepository resurtlepository;
    @Autowired
    private CourseRepository courseRepository;
    private final UserRepo userRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final QuizRepository quizRepository;
    private final Operation_Repo operRepo;
    private final QuestionsRepo questionsRepo;

    /**
     * Strategy Pattern — injected grading strategy.
     * Defaults to "percentageGrading" bean. Can be swapped by changing
     * the @Qualifier to "passFailGrading" or "curvedGrading" without
     * touching any other code.
     */
    @Autowired
    @Qualifier("percentageGrading")
    private GradingStrategy gradingStrategy;
    
@Autowired
private InstructorRepo instructorRepo;

    public UserService(UserRepo userRepo,
                       EnrollmentRepo enrollmentRepo,
                       QuizRepository quizRepository,
                       Operation_Repo operRepo,
                       Resurtlepository resurtlepository,
                       QuestionsRepo questionsRepo) {
        this.userRepo = userRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.quizRepository = quizRepository;
        this.operRepo = operRepo;
        this.resurtlepository = resurtlepository;
        this.questionsRepo = questionsRepo;
    }

//

//public AuthDTO userLogin(Users users) throws Exception {
//    Optional<Users> existingUser = userRepo.findByEmail(users.getEmail());
//    Optional<Instructor> instructorOpt = instructorRepo.findByEmail(users.getEmail());
//
//    if (existingUser.isEmpty() && instructorOpt.isEmpty()) {
//        throw new Exception("User not found");
//    }
//
//    AuthDTO authDTO = new AuthDTO();
//
//    if (instructorOpt.isPresent()) {
//        Instructor instr = instructorOpt.get();
//        if (!instr.getPassword().equals(users.getPassword())) {
//            throw new Exception("Invalid password");
//        }
//
//        authDTO.setUser_id(instr.getInstructorId());
//        authDTO.setEmail(instr.getEmail());
//        authDTO.setRole("instructor");
//    } else {
//        Users dbUser = existingUser.get();
//        if (!dbUser.getPassword().equals(users.getPassword())) {
//            throw new Exception("Invalid password");
//        }
//
//        authDTO.setUser_id(dbUser.getUser_id());
//        authDTO.setEmail(dbUser.getEmail());
//        authDTO.setRole(dbUser.getRole());
//    }
//
//    return authDTO;
//}
public AuthDTO userLogin(Users users) throws Exception {
    Optional<Users> existingUser = userRepo.findByEmail(users.getEmail());
    Optional<Instructor> instructorOpt = instructorRepo.findByEmail(users.getEmail());

    if (existingUser.isEmpty() && instructorOpt.isEmpty()) {
        throw new Exception("User not found");
    }

    AuthDTO authDTO = new AuthDTO();

    if (instructorOpt.isPresent()) {
        Instructor instr = instructorOpt.get();
        if (!instr.getPassword().equals(users.getPassword())) {
            throw new Exception("Invalid password");
        }
        authDTO.setUser_id(instr.getInstructorId());
        authDTO.setEmail(instr.getEmail());
        authDTO.setRole("instructor");
    } else {
        Users dbUser = existingUser.get();
        if (!dbUser.getPassword().equals(users.getPassword())) {
            throw new Exception("Invalid password");
        }
        authDTO.setUser_id(dbUser.getUser_id());
        authDTO.setEmail(dbUser.getEmail());
        authDTO.setRole(dbUser.getRole());
    }

    try (Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/learning_platform", "root", "harsha@123")) {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT User FROM mysql.user WHERE User = ?");
        stmt.setString(1, authDTO.getRole() + "_user");
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            throw new Exception("Database user for role not found");
        }
    } catch (SQLException e) {
        throw new Exception("Error verifying DB user privileges: " + e.getMessage());
    }

    return authDTO;
}


    /**
     * Factory Pattern (Creational) — UserFactory.create() decides whether to
     * instantiate a Users or an Instructor based on the role field.
     * The service no longer contains manual new Instructor() / new Users()
     * construction logic; that responsibility lives in UserFactory.
     */
    @Transactional
    public void registerUser(Users users) {
        Object created = UserFactory.create(
                users.getFname(),
                users.getLname(),
                users.getEmail(),
                users.getPassword(),
                users.getRole()
        );

        if (created instanceof Instructor) {
            instructorRepo.save((Instructor) created);
        } else {
            userRepo.save((Users) created);
        }
    }


    public void addCourse(Course course) {
        operRepo.save(course);
    }

 @Transactional
public Quiz addQuiz(Quiz quiz) {
    Course course = courseRepository.findById(quiz.getCourse().getCourse_id())
            .orElseThrow(() -> new RuntimeException("Course not found"));
    quiz.setCourse(course);

    for (Question question : quiz.getQuestions()) {
        question.setQuiz(quiz);

        for (Option option : question.getOptions()) {
            option.setQuestion(question);
            // manually convert incoming 0/1 or true/false from frontend
            option.setCorrect(Boolean.TRUE.equals(option.isCorrect()));
        }
    }

    return quizRepository.save(quiz);
}


    @Transactional
    public void enroll(String email, int courseId) {
        Users user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = operRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);

        enrollmentRepo.save(enrollment);
    }

    public List<ResultDTO> getQuizResultsByEmail(String email) {
        Users user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return resurtlepository.findQuizResultsByUserId(user.getUser_id());
    }

    public List<QuestionsDTO> getQuestionsByQuizId(int quizId) {
        List<Question> questions = questionsRepo.findByQuizIdWithOptions(quizId);

        return questions.stream().map(q -> {
            List<String> optionTexts = q.getOptions().stream()
                                        .map(Option::getOptionText)
                                        .collect(Collectors.toList());

            String correct = q.getOptions().stream()
                              .filter(Option::isCorrect)
                              .map(Option::getOptionText)
                              .findFirst()
                              .orElse("");

            return new QuestionsDTO(q.getQuestionText(), optionTexts, correct);
        }).collect(Collectors.toList());
    }


    public List<QuizDTO> getAllQuizzes() {
    return quizRepository.findAll()
            .stream()
            .map(q -> new QuizDTO(q.getQuiz_id(), q.getTitle(), q.getTotal_marks()))
            .collect(Collectors.toList());
    }


    public void saveResult(ResultDTO resultDTO) {
        Users user = userRepo.findById(resultDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Quiz quiz = quizRepository.findById(resultDTO.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Result result = new Result();
        result.setUser(user);
        result.setQuiz(quiz);
        result.setScore(resultDTO.getScore());
        result.setFeedback(resultDTO.getFeedback());

        resurtlepository.save(result);
    }


    @Autowired
    private DiscussionRepo discussionRepo;

public List<DiscussionDTO> get_chat(Integer quiz_id) {
    List<Chat> chats = discussionRepo.findByQuizId(quiz_id);
    return chats.stream()
        .map(c -> new DiscussionDTO(
            c.getId(),
            c.getTopic(),
            c.getUser().getUser_id(),
            c.getParentDiscussion() != null ? c.getParentDiscussion().getId() : null,
            c.getQuizId()
        )
        )
        .collect(Collectors.toList());
}
public void reply(@RequestBody DiscussionDTO discussionDTO) {
    Chat chat = new Chat();
    chat.setTopic(discussionDTO.getTopic());

    Users user = userRepo.findById(discussionDTO.getUser_id())
            .orElseThrow(() -> {
                return new RuntimeException("User not found");
            });

    Chat parent = null;
    if (discussionDTO.getParent_Discussion_Id() != null) {
        parent = discussionRepo.findById(discussionDTO.getParent_Discussion_Id())
                .orElseThrow(() -> {
                    return new RuntimeException("Parent discussion not found");
                });
    }

    chat.setUser(user);
    chat.setParentDiscussion(parent);
    chat.setQuizId(discussionDTO.getQuiz_id());

    discussionRepo.save(chat);
}

public void deleteCourse(Integer id) {
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Course not found");
        }
        courseRepository.deleteById(id);
    }


    public  void deleteInstructor(Integer id) {
        if (!instructorRepo.existsById(id)) {
            throw new RuntimeException("Instructor not found");
        }
        instructorRepo.deleteById(id);
    }

    /**
     * Strategy Pattern (Behavioral) — delegates grade calculation to the
     * injected GradingStrategy. The algorithm (percentage, pass/fail, curved)
     * is chosen at configuration time via @Qualifier and can be changed
     * without modifying this class.
     */
    public String calculateGrade(double avgScore) {
        return gradingStrategy.calculateGrade(avgScore);
    }

    /**
     * Allow runtime switching of the grading strategy if needed.
     * For example, an admin endpoint could call this to switch to curved grading.
     */
    public void setGradingStrategy(GradingStrategy gradingStrategy) {
        this.gradingStrategy = gradingStrategy;
    }

}
