//package lk.ijse.gdse72.backend.controller;
//
//import lk.ijse.gdse72.backend.entity.Course;
//import lk.ijse.gdse72.backend.service.CourseService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/user/courses")
//@CrossOrigin(origins = "*") // Allow frontend requests
//public class UserCourseController {
//
//    @Autowired
//    private CourseService courseService;
//
//    @GetMapping("/getall")
//    public List<Course> getAllCourses() {
//        return courseService.getAllCourses();
//    }
//
//    @GetMapping("/{id}")
//    public Course getCourseById(@PathVariable Long id) {
//        return courseService.getCourseById(id)
//                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
//    }
//
//    @GetMapping("/search")
//    public List<Course> searchCourses(@RequestParam String query) {
//        return courseService.searchCourses(query);
//    }
////
//    @GetMapping("/subject/{subject}")
//    public List<Course> getCoursesBySubject(@PathVariable String subject) {
//        return courseService.getCoursesBySubject(subject);
//    }
//}