# Design Patterns & Principles — E-Learning Platform

## Architecture Pattern

### MVC (Model-View-Controller)
The application follows a strict 3-tier MVC architecture:

| Layer | Role | Classes |
|---|---|---|
| **Controller** | Handles HTTP requests, returns responses | `CourseController`, `LoginController`, `UserController`, `StatsController` |
| **Service (Model logic)** | Business rules, orchestration | `UserService` |
| **Repository (Model data)** | Data access abstraction | `UserRepo`, `CourseRepository`, `EnrollmentRepo`, `QuizRepository`, etc. |
| **View** | React SPA frontend | All `.jsx` files in `FrontEnd/` |

Controllers never access Repositories directly — they always delegate to `UserService`, maintaining clean separation.

---

## Design Patterns

### 1. Facade Pattern — Structural ✅

**Class:** `UserService.java`

**Intent:** Provide a simplified, unified interface to a complex subsystem of classes.

**How it is applied:** `UserService` is a single class that hides the complexity of interacting with 8+ repositories (`UserRepo`, `EnrollmentRepo`, `QuizRepository`, `QuestionsRepo`, `DiscussionRepo`, `InstructorRepo`, `CourseRepository`, `Resurtlepository`). All four controllers call only `UserService` — they have no direct dependency on any repository. This is a textbook Facade: the subsystem (repositories + domain rules) is complex; the Facade (UserService) makes it simple.

```
CourseController ──────────────────────────┐
LoginController  ──→  UserService (Facade) ──→ [UserRepo, EnrollmentRepo,
UserController   ──────────────────────────┘    CourseRepository, QuizRepo…]
StatsController
```

---

### 2. Repository Pattern — Structural ✅ *(Framework-enforced)*

**Classes:** `UserRepo`, `CourseRepository`, `EnrollmentRepo`, `QuizRepository`, `QuestionsRepo`, `DiscussionRepo`, `InstructorRepo`, `Resurtlepository`

**Intent:** Mediate between the domain and data-mapping layers using a collection-like interface for accessing domain objects.

**How it is applied:** Each entity has its own Repository interface extending `JpaRepository<Entity, ID>`. Spring Data JPA generates the concrete implementation at runtime. Callers treat repositories as simple in-memory collections — they call `save()`, `findById()`, `findAll()` etc. without knowing anything about SQL or Hibernate. Custom queries are declared as method signatures or JPQL `@Query` annotations, not written as SQL strings in the service.

---

### 3. Strategy Pattern — Behavioral ✅

**Classes:** `GradingStrategy` (interface), `PercentageGradingStrategy`, `PassFailGradingStrategy`, `CurvedGradingStrategy`

**Intent:** Define a family of algorithms, encapsulate each one, and make them interchangeable. Strategy lets the algorithm vary independently from clients that use it.

**How it is applied:** `UserService.calculateGrade()` previously contained a hardcoded if-else chain. This was refactored to delegate to a `GradingStrategy` interface injected via Spring's `@Qualifier`. Three concrete strategies exist:

| Strategy | Description | When to use |
|---|---|---|
| `PercentageGradingStrategy` | A/B/C/D/F scale (90/80/70/60 thresholds) | Default for all courses |
| `PassFailGradingStrategy` | Pass (≥50) or Fail | Orientation / compliance quizzes |
| `CurvedGradingStrategy` | Adds 10-point curve before grading | When an instructor adjusts difficulty |

Switching the strategy requires only changing the `@Qualifier` annotation — no other code changes needed. The grading algorithm is completely decoupled from `UserService`.

```java
// In UserService — only knows about the interface, not the concrete class
@Autowired
@Qualifier("percentageGrading")   // ← change this to swap the algorithm
private GradingStrategy gradingStrategy;

public String calculateGrade(double score) {
    return gradingStrategy.calculateGrade(score);  // delegates entirely
}
```

---

### 4. Factory Method Pattern — Creational ✅

**Class:** `UserFactory.java`

**Intent:** Define an interface for creating an object, but let the factory decide which class to instantiate. Factory Method lets a class defer instantiation to a factory.

**How it is applied:** When a user registers, the system must create either a `Users` entity (for students and admins) or an `Instructor` entity — two different classes with different fields. Previously `UserService.registerUser()` contained manual `new Instructor()` / `new Users()` construction logic with role checks mixed in. `UserFactory.create()` centralises and encapsulates this decision. `UserService` simply calls the factory and saves whatever comes back.

```java
// Before (scattered construction in service):
if ("admin".equals(role) || "student".equals(role)) {
    Users u = new Users(); u.setFname(...); ...
} else {
    Instructor i = new Instructor(); i.setFname(...); ...
}

// After (factory encapsulates the decision):
Object created = UserFactory.create(fname, lname, email, password, role);
if (created instanceof Instructor) instructorRepo.save((Instructor) created);
else userRepo.save((Users) created);
```

---

### 5. Builder Pattern — Creational ✅

**Class:** `QuizBuilder.java`

**Intent:** Separate the construction of a complex object from its representation so that the same construction process can produce different representations.

**How it is applied:** A `Quiz` is a complex object — it has a title, type, total marks, a linked `Course`, and a list of `Question` objects each of which has its own list of `Option` objects with correct/incorrect flags and back-references. `QuizBuilder` constructs this entire object graph step-by-step using a fluent API, auto-accumulating `totalMarks` and wiring all back-references before returning a ready-to-persist `Quiz`.

```java
Quiz quiz = new QuizBuilder()
    .title("Java OOP Quiz")
    .type("MCQ")
    .course(course)
    .addQuestion("What is polymorphism?", 10,
        List.of("Multiple forms", "Single form", "No form", "All forms"),
        "Multiple forms")
    .addQuestion("What does JVM stand for?", 5,
        List.of("Java Virtual Machine", "Java Variable Method",
                "Just Virtual Method", "Java Variable Module"),
        "Java Virtual Machine")
    .build();  // validates required fields and wires all back-references
```

---

### 6. Chain of Responsibility — Behavioral ✅

**Class:** `SpringSecurity.java`

**Intent:** Avoid coupling the sender of a request to its receiver by giving more than one object a chance to handle it. Chain the receiving objects and pass the request along until one handles it.

**How it is applied:** Every HTTP request passes through Spring Security's `SecurityFilterChain` — a literal chain of filter handlers:
1. **CorsFilter** — checks the `Origin` header; rejects if not `http://localhost:5173`
2. **CSRF filter** — disabled (stateless REST API)
3. **Authorization check** — permits all requests through (role checks done client-side)

Each filter either handles the request (by rejecting it) or passes it to the next in the chain.

---

## Design Principles

### 1. Single Responsibility Principle (SRP)
Each controller class owns exactly one domain area. `CourseController` handles only course operations. `LoginController` handles only authentication. `StatsController` handles only analytics. `UserController` handles only quiz, enrollment, result, and discussion operations. No class mixes concerns from multiple domains.

### 2. Open/Closed Principle (OCP)
The `JpaRepository` interfaces are **open for extension** (add new query methods by declaring them in the interface) but **closed for modification** (the base CRUD implementation never changes). Similarly, adding a new `GradingStrategy` requires creating a new class — no existing code is modified.

### 3. Dependency Inversion Principle (DIP)
High-level modules (controllers) depend on abstractions (`UserService`), not on concrete repositories. `UserService` depends on the `GradingStrategy` interface, not on `PercentageGradingStrategy` directly. Spring's `@Autowired` wires concrete implementations at runtime — the source code never references them directly.

### 4. Don't Repeat Yourself (DRY)
All user-registration logic lives in one place (`UserFactory`). All grading logic lives in one place (the injected `GradingStrategy`). All business rules live in `UserService`. If a rule changes, it changes in one class only.
