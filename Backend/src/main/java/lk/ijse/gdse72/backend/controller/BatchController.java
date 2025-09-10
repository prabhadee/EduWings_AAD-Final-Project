package lk.ijse.gdse72.backend.controller;

import lk.ijse.gdse72.backend.dto.BatchDTO;
import lk.ijse.gdse72.backend.dto.InstructorDTO;
import lk.ijse.gdse72.backend.entity.Course;
import lk.ijse.gdse72.backend.service.BatchService;
import lk.ijse.gdse72.backend.service.CourseService;
import lk.ijse.gdse72.backend.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/batches")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BatchController {

    private final BatchService batchService;
    private final CourseService courseService;
    private final InstructorService instructorService;

    @PostMapping("/create")
    public BatchDTO createBatch(@RequestBody BatchDTO batchDTO) {
        return batchService.createBatch(batchDTO);
    }

    @PutMapping("/update/{id}")
    public BatchDTO updateBatch(@PathVariable Long id, @RequestBody BatchDTO batchDTO) {
        return batchService.updateBatch(id, batchDTO);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteBatch(@PathVariable Long id) {
        batchService.deleteBatch(id);
        return "Batch deleted successfully!";
    }

    @GetMapping("/{id}")
    public BatchDTO getBatchById(@PathVariable Long id) {
        return batchService.getBatchById(id);
    }

    @GetMapping("/all")
    public List<BatchDTO> getAllBatches() {
        return batchService.getAllBatches();
    }

    @GetMapping("/courses")
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/instructors")
    public List<InstructorDTO> getAllInstructors() {
        return instructorService.getAllInstructors();
    }


//    @GetMapping("/instructor/{instructorId}")
//    @PreAuthorize("hasAnyRole('USER','ADMIN')")
//    public ResponseEntity<List<BatchDTO>> getBatchesByInstructorId(@PathVariable Long instructorId) {
//        List<BatchDTO> batches = batchService.getBatchesByInstructorId(instructorId);
//        return ResponseEntity.ok(batches);
//    }
}