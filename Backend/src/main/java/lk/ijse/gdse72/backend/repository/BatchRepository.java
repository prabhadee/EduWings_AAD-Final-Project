package lk.ijse.gdse72.backend.repository;

import lk.ijse.gdse72.backend.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {

//    @Query("SELECT b FROM Batch b JOIN FETCH b.course JOIN FETCH b.instructor WHERE b.instructor.id = :instructorId")
//    List<Batch> findBatchesWithDetailsByInstructorId(@Param("instructorId") Long instructorId);
//
//    List<Batch> findByInstructorId(Long instructorId);
List<Batch> findByInstructorId(Long instructorId);

    List<Batch> findByIsActiveTrue();
    List<Batch> findByCourseId(Long courseId);

    Optional<Batch> findByMonthsMonthId(Long monthId);
}