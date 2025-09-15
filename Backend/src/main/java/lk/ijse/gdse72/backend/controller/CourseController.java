package lk.ijse.gdse72.backend.controller;


import lk.ijse.gdse72.backend.dto.CourseDTO;
import lk.ijse.gdse72.backend.entity.Batch;
import lk.ijse.gdse72.backend.entity.Course;
import lk.ijse.gdse72.backend.entity.Instructor;
import lk.ijse.gdse72.backend.repository.BatchRepository;
import lk.ijse.gdse72.backend.repository.CourseRepository;
import lk.ijse.gdse72.backend.repository.InstructorRepository;
import lk.ijse.gdse72.backend.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*") // Allow frontend requests
public class CourseController {

    @Autowired
    private CourseService courseService;
    private CourseRepository courseRepository;
    private InstructorRepository instructorRepository;
    private BatchRepository batchRepository;
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addcourse")
    public Course addCourse(@RequestBody CourseDTO courseDTO) {
        return courseService.addCourse(courseDTO);
    }

    @GetMapping("/getall")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Long id, @RequestBody CourseDTO courseDTO) {
        return courseService.updateCourse(id, courseDTO);
    }

    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "Course deleted successfully!";
    }

    @GetMapping("") // Changed from "/api/courses"
    public ResponseEntity<List<Course>> getActiveCourses() {
        List<Course> courses = courseRepository.findByIsActiveTrue();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}") // Changed from "/api/courses/{id}"
    public ResponseEntity<Course> getCourse(@PathVariable Long id) {
        Optional<Course> course = courseRepository.findById(id);
        return course.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/instructors") // Changed from "/api/courses/{id}/instructors"
    public ResponseEntity<List<Instructor>> getCourseInstructors(@PathVariable Long id) {
        List<Instructor> instructors = instructorRepository.findByCourseId(id);
        return ResponseEntity.ok(instructors);
    }

    @GetMapping("/{id}/batches") // Changed from "/api/courses/{id}/batches"
    public ResponseEntity<List<Batch>> getCourseBatches(@PathVariable Long id) {
        List<Batch> batches = batchRepository.findByCourseId(id);
        return ResponseEntity.ok(batches);
    }
}
