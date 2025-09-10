package lk.ijse.gdse72.backend.service;

import lk.ijse.gdse72.backend.dto.InstructorDTO;
import lk.ijse.gdse72.backend.entity.Instructor;

import java.util.List;

public interface InstructorService {
    InstructorDTO saveInstructor(InstructorDTO instructorDTO);
    InstructorDTO updateInstructor(InstructorDTO instructorDTO);
    void deleteInstructor(Long id);
    InstructorDTO getInstructorById(Long id);
    List<InstructorDTO> getAllInstructors();

//    void assignCourseToInstructor(Long instructorId, Long courseId);
//
//    void unassignCourseFromInstructor(Long instructorId, Long courseId);
//
//    List<InstructorDTO> getInstructorCourses(Long instructorId);
List<Instructor> getInstructorsByCourseId(Long courseId);
}
