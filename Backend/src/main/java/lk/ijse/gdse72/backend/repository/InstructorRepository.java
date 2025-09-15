package lk.ijse.gdse72.backend.repository;

import lk.ijse.gdse72.backend.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    boolean existsByEmail(String email);

    // Add this if you need to find by email for validation
    Optional<Instructor> findByEmail(String email);
    List<Instructor> findByCourseId(Long courseId);

    List<Instructor> findByIsActiveTrue();
}
