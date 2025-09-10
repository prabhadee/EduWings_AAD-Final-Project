package lk.ijse.gdse72.backend.service.Impl;

import lk.ijse.gdse72.backend.dto.BatchMonthDTO;
import lk.ijse.gdse72.backend.entity.Batch;
import lk.ijse.gdse72.backend.entity.BatchMonth;
import lk.ijse.gdse72.backend.repository.BatchMonthRepository;
import lk.ijse.gdse72.backend.repository.BatchRepository;
import lk.ijse.gdse72.backend.service.BatchMonthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatchMonthServiceImpl implements BatchMonthService {

    private final BatchRepository batchRepository;
    private final BatchMonthRepository monthRepository;

    @Override
    public BatchMonthDTO createMonth(Long batchId, BatchMonthDTO dto) {

        if (batchId == null) {
            throw new IllegalArgumentException("Batch ID cannot be null");
        }
        if (dto.getMonthName() == null || dto.getMonthName().trim().isEmpty()) {
            throw new IllegalArgumentException("Month name cannot be empty");
        }
        try {
            Batch batch = batchRepository.findById(batchId)
                    .orElseThrow(() -> new RuntimeException("Batch not found with id: " + batchId));

            BatchMonth month = BatchMonth.builder()
                    .monthName(dto.getMonthName())
                    .batch(batch)
                    .build();

            BatchMonth savedMonth = monthRepository.save(month);

            return convertToDTO(savedMonth);

        } catch (Exception e) {
            throw new RuntimeException("Error creating month: " + e.getMessage(), e);
        }
    }

    @Override
    public BatchMonthDTO updateMonth(Long monthId, BatchMonthDTO dto) {
        try {
            BatchMonth month = monthRepository.findById(monthId)
                    .orElseThrow(() -> new RuntimeException("Month not found with id: " + monthId));

            month.setMonthName(dto.getMonthName());
            BatchMonth updatedMonth = monthRepository.save(month);

            return convertToDTO(updatedMonth);

        } catch (Exception e) {
            throw new RuntimeException("Error updating month: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteMonth(Long monthId) {
        try {
            if (!monthRepository.existsById(monthId)) {
                throw new RuntimeException("Month not found with id: " + monthId);
            }
            monthRepository.deleteById(monthId);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting month: " + e.getMessage(), e);
        }
    }

    @Override
    public BatchMonthDTO getMonthById(Long monthId) {
        try {
            BatchMonth month = monthRepository.findById(monthId)
                    .orElseThrow(() -> new RuntimeException("Month not found with id: " + monthId));

            return convertToDTO(month);

        } catch (Exception e) {
            throw new RuntimeException("Error fetching month: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BatchMonthDTO> getMonthsByBatch(Long batchId) {
        try {
            // Use the repository method you already defined
            return monthRepository.findByBatch_BatchId(batchId).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching months by batch: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BatchMonthDTO> getAllMonths() {
        try {
            return monthRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all months: " + e.getMessage(), e);
        }
    }

    // Helper method to convert Entity to DTO
    private BatchMonthDTO convertToDTO(BatchMonth month) {
        return BatchMonthDTO.builder()
                .monthId(month.getMonthId())
                .monthName(month.getMonthName())
                .batchId(month.getBatch().getBatchId())
                .build();
    }
}