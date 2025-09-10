package lk.ijse.gdse72.backend.controller;


import lk.ijse.gdse72.backend.dto.CourseDTO;
import lk.ijse.gdse72.backend.entity.Course;
import lk.ijse.gdse72.backend.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*") // Allow frontend requests
public class CourseController {

    @Autowired
    private CourseService courseService;
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
}
