package lk.ijse.gdse72.backend.service.Impl;

import lk.ijse.gdse72.backend.dto.BatchDTO;
import lk.ijse.gdse72.backend.entity.Batch;
import lk.ijse.gdse72.backend.entity.Course;
import lk.ijse.gdse72.backend.entity.Instructor;
import lk.ijse.gdse72.backend.repository.BatchRepository;
import lk.ijse.gdse72.backend.repository.CourseRepository;
import lk.ijse.gdse72.backend.repository.InstructorRepository;
import lk.ijse.gdse72.backend.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatchServiceImpl implements BatchService {

    private final BatchRepository batchRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final ModelMapper modelMapper;


    @Override
    public BatchDTO createBatch(BatchDTO batchDTO) {
        // Fetch the related Course and Instructor entities
        Course course = courseRepository.findById(batchDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Instructor instructor = instructorRepository.findById(batchDTO.getInstructorId())
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        // Build the Batch entity using the resolved entities
        Batch batch = Batch.builder()
                .batchName(batchDTO.getBatchName())
                .monthlyFee(batchDTO.getMonthlyFee())
                .course(course)
                .instructor(instructor)
                .build();

        // Save batch
        Batch savedBatch = batchRepository.save(batch);

        // Convert to DTO manually (no batchMapper needed)
        return BatchDTO.builder()
                .batchId(savedBatch.getBatchId())
                .batchName(savedBatch.getBatchName())
                .monthlyFee(savedBatch.getMonthlyFee())
                .courseId(savedBatch.getCourse().getId())
                .instructorId(savedBatch.getInstructor().getId())
                .build();
    }

//    public BatchDTO createBatch(BatchDTO batchDTO) {
//        Course course = courseRepository.findById(batchDTO.getCourseId())
//                .orElseThrow(() -> new RuntimeException("Course not found"));
//
//        Instructor instructor = instructorRepository.findById(batchDTO.getInstructorId())
//                .orElseThrow(() -> new RuntimeException("Instructor not found"));
//
//        Batch batch = modelMapper.map(batchDTO, Batch.class);
//        batch.setCourse(course);
//        batch.setInstructor(instructor);
//
//        Batch savedBatch = batchRepository.save(batch);
//        return convertToDTO(savedBatch);
//    }


    @Override
    public BatchDTO updateBatch(Long id, BatchDTO batchDTO) {
        Batch existingBatch = batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        Course course = courseRepository.findById(batchDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Instructor instructor = instructorRepository.findById(batchDTO.getInstructorId())
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        existingBatch.setBatchName(batchDTO.getBatchName());
        existingBatch.setMonthlyFee(batchDTO.getMonthlyFee());
        existingBatch.setCourse(course);
        existingBatch.setInstructor(instructor);

        Batch updatedBatch = batchRepository.save(existingBatch);

        return BatchDTO.builder()
                .batchId(updatedBatch.getBatchId())
                .batchName(updatedBatch.getBatchName())
                .monthlyFee(updatedBatch.getMonthlyFee())
                .courseId(updatedBatch.getCourse().getId())
                .instructorId(updatedBatch.getInstructor().getId())
                .build();
    }

//    public BatchDTO updateBatch(Long id, BatchDTO batchDTO) {
//        Batch existingBatch = batchRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Batch not found"));
//
//        Course course = courseRepository.findById(batchDTO.getCourseId())
//                .orElseThrow(() -> new RuntimeException("Course not found"));
//
//        Instructor instructor = instructorRepository.findById(batchDTO.getInstructorId())
//                .orElseThrow(() -> new RuntimeException("Instructor not found"));
//
//        existingBatch.setBatchName(batchDTO.getBatchName());
//        existingBatch.setMonthlyFee(batchDTO.getMonthlyFee());
//        existingBatch.setCourse(course);
//        existingBatch.setInstructor(instructor);
//
//        Batch updatedBatch = batchRepository.save(existingBatch);
//        return convertToDTO(updatedBatch);
//    }

    @Override
    public void deleteBatch(Long batchId) {
        if (!batchRepository.existsById(batchId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch not found");
        }
        batchRepository.deleteById(batchId);
    }

//    @Override
//    public BatchDTO getBatchById(Long batchId) {
//        Batch batch = batchRepository.findById(batchId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch not found"));
//        return mapToDTO(batch);
//    }

    @Override
    public List<BatchDTO> getAllBatches() {
        return batchRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private BatchDTO mapToDTO(Batch batch) {
        return BatchDTO.builder()
                .batchId(batch.getBatchId())
                .batchName(batch.getBatchName())
                .monthlyFee(batch.getMonthlyFee())
                .courseId(batch.getCourse().getId())       // ✅ Fixed
                .instructorId(batch.getInstructor().getId()) // ✅ Fixed
                .build();
    }
//    private BatchDTO convertToDTO(Batch batch) {
//        BatchDTO dto = modelMapper.map(batch, BatchDTO.class);
//        dto.setCourseId(batch.getCourse().getId());
//        dto.setInstructorId(batch.getInstructor().getId());
//        return dto;
//    }

//    @Override
//    public List<BatchDTO> getBatchesByInstructorId(Long instructorId) {
//        List<Batch> batches = batchRepository.findBatchesWithDetailsByInstructorId(instructorId);
//
//        return batches.stream()
//                .map(this::mapToDTO)
//                .collect(Collectors.toList());
//    }
//
//
//    private BatchDTO mapToDTO(Batch batch) {
//        Set<Long> monthIds = batch.getMonths().stream()
//                .map(month -> month.getMonthId())
//                .collect(Collectors.toSet());
//
//        return BatchDTO.builder()
//                .batchId(batch.getBatchId())
//                .batchName(batch.getBatchName())
//                .monthlyFee(batch.getMonthlyFee())
//                .courseId(batch.getCourse().getId())
//                .instructorId(batch.getInstructor().getId())
//                .monthIds(monthIds)
//                .build();
//    }
//    @Override
//    public List<Batch> getBatchEntitiesByInstructorId(Long instructorId) {
//        return batchRepository.findByInstructorId(instructorId);
//    }
@Override
public List<BatchDTO> getBatchesByInstructorId(Long instructorId) {
    List<Batch> batches = batchRepository.findByInstructorId(instructorId);
    return batches.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
}

    @Override
    public BatchDTO getBatchById(Long batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));
        return convertToDTO(batch);
    }

    private BatchDTO convertToDTO(Batch batch) {
        return BatchDTO.builder()
                .batchId(batch.getBatchId())
                .batchName(batch.getBatchName())
                .monthlyFee(batch.getMonthlyFee())
                .courseId(batch.getCourse().getId())
                .instructorId(batch.getInstructor().getId())
                .build();
    }

}
