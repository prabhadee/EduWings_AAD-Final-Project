package lk.ijse.gdse72.backend.service.Impl;

import lk.ijse.gdse72.backend.dto.InstructorDTO;
import lk.ijse.gdse72.backend.entity.Course;
import lk.ijse.gdse72.backend.entity.Instructor;
import lk.ijse.gdse72.backend.repository.CourseRepository;
import lk.ijse.gdse72.backend.repository.InstructorRepository;
import lk.ijse.gdse72.backend.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorServiceImpl implements InstructorService {
@Autowired
    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;

    // Convert entity → DTO
    private InstructorDTO mapToDTO(Instructor instructor) {
        return InstructorDTO.builder()
                .id(instructor.getId())
                .name(instructor.getName())
                .email(instructor.getEmail())
                .phone(instructor.getPhone())
                .photo(instructor.getPhoto())
                .courseId(instructor.getCourse().getId())
                .build();
    }

    // Convert DTO → entity
    private Instructor mapToEntity(InstructorDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return Instructor.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .photo(dto.getPhoto())
                .course(course)
                .build();
    }

    @Override
    public InstructorDTO saveInstructor(InstructorDTO instructorDTO) {
        // Check email uniqueness before save
        if (instructorRepository.existsByEmail(instructorDTO.getEmail())) {
            throw new RuntimeException("Instructor email already exists");
        }

        Optional<Course> course = courseRepository.findById(instructorDTO.getCourseId());
        Instructor instructor = Instructor.builder()
                .name(instructorDTO.getName())
                .phone(instructorDTO.getPhone())
                .email(instructorDTO.getEmail())
                .photo(instructorDTO.getPhoto())
                .course(course.orElseThrow(() -> new RuntimeException("Course not found")))
                .build();

        return mapToDTO(instructorRepository.save(instructor));
    }

    @Override
    public InstructorDTO updateInstructor(InstructorDTO instructorDTO) {
        if (!instructorRepository.existsById(instructorDTO.getId())) {
            throw new RuntimeException("Instructor not found");
        }

        // Check if email is being changed and if it already exists for another instructor
        Instructor existingInstructor = instructorRepository.findById(instructorDTO.getId())
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        if (!existingInstructor.getEmail().equals(instructorDTO.getEmail()) &&
                instructorRepository.existsByEmail(instructorDTO.getEmail())) {
            throw new RuntimeException("Email already exists for another instructor");
        }

        Instructor instructor = mapToEntity(instructorDTO);
        return mapToDTO(instructorRepository.save(instructor));
    }

    @Override
    public void deleteInstructor(Long id) {
        if (!instructorRepository.existsById(id)) {
            throw new RuntimeException("Instructor not found");
        }
        instructorRepository.deleteById(id);
    }

    @Override
    public InstructorDTO getInstructorById(Long id) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        return mapToDTO(instructor);
    }

    @Override
    public List<InstructorDTO> getAllInstructors() {
        return instructorRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    @Override
    public List<Instructor> getInstructorsByCourseId(Long courseId) {
        return instructorRepository.findByCourseId(courseId);
    }
}
