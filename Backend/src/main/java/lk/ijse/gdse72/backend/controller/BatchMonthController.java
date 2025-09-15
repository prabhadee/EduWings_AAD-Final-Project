package lk.ijse.gdse72.backend.controller;

import lk.ijse.gdse72.backend.dto.BatchMonthDTO;
import lk.ijse.gdse72.backend.service.BatchMonthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/months")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BatchMonthController {

    private final BatchMonthService batchMonthService;

    @PostMapping("/create")
    public BatchMonthDTO createMonth(@RequestBody BatchMonthDTO dto) {
        return batchMonthService.createMonth(dto.getBatchId(), dto);
    }

    @PutMapping("/update/{monthId}")
    public BatchMonthDTO updateMonth(@PathVariable Long monthId, @RequestBody BatchMonthDTO dto) {
        return batchMonthService.updateMonth(monthId, dto);
    }

    @DeleteMapping("/delete/{monthId}")
    public String deleteMonth(@PathVariable Long monthId) {
        batchMonthService.deleteMonth(monthId);
        return "Month deleted successfully!";
    }

    @GetMapping("/{monthId}")
    public BatchMonthDTO getMonthById(@PathVariable Long monthId) {
        return batchMonthService.getMonthById(monthId);
    }

    @GetMapping("/batch/{batchId}")
    public List<BatchMonthDTO> getMonthsByBatch(@PathVariable Long batchId) {
        return batchMonthService.getMonthsByBatch(batchId);
    }
    @GetMapping("/all")
    public List<BatchMonthDTO> getAllMonths() {
        return batchMonthService.getAllMonths();
    }

    @GetMapping("/batch/{batchId}/simple")
    public ResponseEntity<List<BatchMonthDTO>> getMonthsByBatchSimple(@PathVariable Long batchId) {
        List<BatchMonthDTO> months = batchMonthService.getMonthsByBatch(batchId);
        return ResponseEntity.ok(months);
    }
}
