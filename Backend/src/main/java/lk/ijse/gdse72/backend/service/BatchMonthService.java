package lk.ijse.gdse72.backend.service;

import lk.ijse.gdse72.backend.dto.BatchMonthDTO;
import java.util.List;

public interface BatchMonthService {
    BatchMonthDTO createMonth(Long batchId, BatchMonthDTO dto);
    BatchMonthDTO updateMonth(Long monthId, BatchMonthDTO dto);
    void deleteMonth(Long monthId);
    BatchMonthDTO getMonthById(Long monthId);
    List<BatchMonthDTO> getMonthsByBatch(Long batchId);

    List<BatchMonthDTO> getAllMonths();
}
