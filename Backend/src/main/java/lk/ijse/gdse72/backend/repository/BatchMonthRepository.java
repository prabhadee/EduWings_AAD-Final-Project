package lk.ijse.gdse72.backend.repository;

import lk.ijse.gdse72.backend.entity.BatchMonth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BatchMonthRepository extends JpaRepository<BatchMonth, Long> {
    List<BatchMonth> findByBatch_BatchId(Long batchId);
}
