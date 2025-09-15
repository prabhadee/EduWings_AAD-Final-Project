package lk.ijse.gdse72.backend.service;


import lk.ijse.gdse72.backend.dto.BatchDTO;
import lk.ijse.gdse72.backend.entity.Batch;

import java.util.List;

public interface BatchService {
    BatchDTO createBatch(BatchDTO batchDTO);
    BatchDTO updateBatch(Long batchId, BatchDTO batchDTO);
    void deleteBatch(Long batchId);
    BatchDTO getBatchById(Long batchId);
    List<BatchDTO> getAllBatches();
//    List<BatchDTO> getBatchesByInstructorId(Long instructorId);
//    List<Batch> getBatchEntitiesByInstructorId(Long instructorId);
List<BatchDTO> getBatchesByInstructorId(Long instructorId);

}
