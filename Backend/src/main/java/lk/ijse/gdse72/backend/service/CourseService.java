package lk.ijse.gdse72.backend.service;

import lk.ijse.gdse72.backend.dto.CourseDTO;
import lk.ijse.gdse72.backend.entity.Course;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    Course addCourse(CourseDTO courseDTO);
    List<Course> getAllCourses();

//    Optional<Course> getCourseById(Long id);

    //    Optional<Course> getCourseById(Long id);
    Course updateCourse(Long id, CourseDTO courseDTO);
    void deleteCourse(Long id);

//    List<Course> searchCourses(String query);
//
//    List<Course> getCoursesBySubject(String subject);

    // New methods for user side
//    List<Course> searchCourses(String query);
//    List<Course> getCoursesBySubject(String subject);
}