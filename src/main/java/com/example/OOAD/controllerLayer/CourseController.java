
package com.example.OOAD.controllerLayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.OOAD.Course;
import com.example.OOAD.Instructor;
import com.example.OOAD.RepositoryLayer.CourseRepository;
import com.example.OOAD.RepositoryLayer.InstructorRepo;
import com.example.OOAD.ServiceLayer.UserService;

import java.util.List;

@RestController
public class CourseController {
    @Autowired
    private UserService Userservice;
    @Autowired
    private InstructorRepo instructorRepo;
    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping("/courses")
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @PostMapping("/addcourse")
    public void addCourse(@RequestBody Course course){
    Userservice.addCourse(course);
    }

    @GetMapping("/instructor/all")
    public List<Instructor> getAllInstructors() {
    return instructorRepo.findAll();
    }
    
    @DeleteMapping("/courses/delete/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Integer id) {
        Userservice.deleteCourse(id);
        return ResponseEntity.ok("Course deleted successfully");
    
}
@GetMapping("/instructor/{id}")
public List<Course> getCoursesByInstructor(@PathVariable("id") Integer instructorId) {
    return courseRepository.findByInstructorId(instructorId);
}

}
