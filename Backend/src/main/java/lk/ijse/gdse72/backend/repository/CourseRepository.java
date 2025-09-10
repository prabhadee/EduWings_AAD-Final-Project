package lk.ijse.gdse72.backend.repository;

import lk.ijse.gdse72.backend.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByCourseName(String courseName);

//    // Search courses by name or description
//    List<Course> findByCourseNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
//
//    // Find courses by subject (if you have a subject field)
//    List<Course> findBySubjectIgnoreCase(String subject);
//
//    // Alternative search method using custom query
//    @Query("SELECT c FROM Course c WHERE LOWER(c.courseName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%'))")
//    List<Course> searchCourses(@Param("query") String query);
}