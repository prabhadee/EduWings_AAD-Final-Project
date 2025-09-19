package lk.ijse.gdse72.backend.controller;

import lk.ijse.gdse72.backend.dto.BatchDTO;
import lk.ijse.gdse72.backend.dto.InstructorDTO;
import lk.ijse.gdse72.backend.entity.Batch;
import lk.ijse.gdse72.backend.entity.BatchMonth;
import lk.ijse.gdse72.backend.entity.Course;
import lk.ijse.gdse72.backend.repository.BatchMonthRepository;
import lk.ijse.gdse72.backend.repository.BatchRepository;
import lk.ijse.gdse72.backend.service.BatchService;
import lk.ijse.gdse72.backend.service.CourseService;
import lk.ijse.gdse72.backend.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/batches")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BatchController {

    private final BatchService batchService;
    private final CourseService courseService;
    private final InstructorService instructorService;
    private final BatchRepository batchRepository;;
    private final BatchMonthRepository batchMonthRepository;

    @GetMapping("/by-month/{monthId}")
    public ResponseEntity<BatchDTO> getBatchByMonthId(@PathVariable Long monthId) {
        BatchMonth month = batchMonthRepository.findById(monthId)
                .orElseThrow(() -> new RuntimeException("Month not found with id: " + monthId));

        Batch batch = month.getBatch();
        BatchDTO batchDTO = BatchDTO.builder()
                .batchId(batch.getBatchId())
                .batchName(batch.getBatchName())
                .monthlyFee(batch.getMonthlyFee())
                .courseId(batch.getCourse().getId())
                .instructorId(batch.getInstructor().getId())
                .build();

        return ResponseEntity.ok(batchDTO);
    }

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

//    @GetMapping("/{id}")
//    public BatchDTO getBatchById(@PathVariable Long id) {
//        return batchService.getBatchById(id);
//    }

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

//    @GetMapping("/instructor/{instructorId}")
//    @PreAuthorize("hasAnyRole('USER','ADMIN')")
//    public ResponseEntity<List<BatchDTO>> getBatchesByInstructor(@PathVariable Long instructorId) {
//        return ResponseEntity.ok(batchService.getBatchesByInstructorId(instructorId));
//    }
@GetMapping("/instructor/{instructorId}")
public ResponseEntity<List<BatchDTO>> getBatchesByInstructor(@PathVariable Long instructorId) {
    try {
        List<BatchDTO> batches = batchService.getBatchesByInstructorId(instructorId);
        return ResponseEntity.ok(batches);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

    @GetMapping("/{batchId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<BatchDTO> getBatchById(@PathVariable Long batchId) {
        return ResponseEntity.ok(batchService.getBatchById(batchId));
    }
    // Get batch details with months and modules
    @GetMapping("/api/batches/{id}")
    public ResponseEntity<Batch> getBatch(@PathVariable Long id) {
        Optional<Batch> batch = batchRepository.findById(id);

        if (batch.isPresent()) {
            // Eagerly fetch months and modules
            Batch batchObj = batch.get();
            Hibernate.initialize(batchObj.getMonths());
            for (BatchMonth month : batchObj.getMonths()) {
                Hibernate.initialize(month.getModules());
            }
            return ResponseEntity.ok(batchObj);
        }

        return ResponseEntity.notFound().build();
    }

    // Add this to your BatchController
    @GetMapping("/by-month-with-details/{monthId}")
    public ResponseEntity<Map<String, Object>> getBatchByMonthWithDetails(@PathVariable Long monthId) {
        Optional<Batch> batchOpt = batchRepository.findByMonthsMonthId(monthId);

        if (batchOpt.isPresent()) {
            Batch batch = batchOpt.get();

            // Create a response map with all needed data
            Map<String, Object> response = new HashMap<>();
            response.put("batchId", batch.getBatchId());
            response.put("batchName", batch.getBatchName());
            response.put("monthlyFee", batch.getMonthlyFee());

            // Add course details
            if (batch.getCourse() != null) {
                Map<String, Object> courseDetails = new HashMap<>();
                courseDetails.put("id", batch.getCourse().getId());
                courseDetails.put("courseName", batch.getCourse().getCourseName());
                courseDetails.put("description", batch.getCourse().getDescription());
                response.put("course", courseDetails);
            }

            // Add instructor details
            if (batch.getInstructor() != null) {
                Map<String, Object> instructorDetails = new HashMap<>();
                instructorDetails.put("id", batch.getInstructor().getId());
                instructorDetails.put("name", batch.getInstructor().getName());
                instructorDetails.put("email", batch.getInstructor().getEmail());
                instructorDetails.put("phone", batch.getInstructor().getPhone());
                instructorDetails.put("photo", batch.getInstructor().getPhoto());
                response.put("instructor", instructorDetails);
            }

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.notFound().build();
    }
}