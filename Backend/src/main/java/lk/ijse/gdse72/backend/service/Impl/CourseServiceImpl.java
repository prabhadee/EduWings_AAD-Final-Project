package lk.ijse.gdse72.backend.service.Impl;

import lk.ijse.gdse72.backend.dto.CourseDTO;
import lk.ijse.gdse72.backend.entity.Course;
import lk.ijse.gdse72.backend.repository.CourseRepository;
import lk.ijse.gdse72.backend.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public Course addCourse(CourseDTO courseDTO) {
        if (courseRepository.existsByCourseName(courseDTO.getCourseName())) {
            throw new RuntimeException("Course already exists!");
        }
        Course course = Course.builder()
                .courseName(courseDTO.getCourseName())
                .description(courseDTO.getDescription())
                .build();
        return courseRepository.save(course);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

//    @Override
//    public Optional<Course> getCourseById(Long id) {
//        return courseRepository.findById(id);
//    }

    @Override
    public Course updateCourse(Long id, CourseDTO courseDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found!"));
        course.setCourseName(courseDTO.getCourseName());
        course.setDescription(courseDTO.getDescription());
        return courseRepository.save(course);
    }

    @Override
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

//    @Override
//    public List<Course> searchCourses(String query) {
//        return courseRepository.findByCourseNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
//    }
//
//    @Override
//    public List<Course> getCoursesBySubject(String subject) {
//        // This assumes you have a subject field in your Course entity
//        // If not, you'll need to add it or modify this method
//        return courseRepository.findBySubjectIgnoreCase(subject);
//    }
}