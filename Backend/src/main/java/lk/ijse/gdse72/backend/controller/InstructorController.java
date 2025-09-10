package lk.ijse.gdse72.backend.controller;

import lk.ijse.gdse72.backend.dto.InstructorDTO;
import lk.ijse.gdse72.backend.entity.Instructor;
import lk.ijse.gdse72.backend.service.Impl.CloudinaryService;
import lk.ijse.gdse72.backend.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/instructors")
@CrossOrigin(origins = "*") // Allow frontend requests - consistent with CourseController
public class InstructorController {

    @Autowired
    private InstructorService instructorService;
    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/create")
    public ResponseEntity<InstructorDTO> createInstructor(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam Long courseId,
            @RequestParam(value = "photo", required = false) MultipartFile photoFile
    ) throws IOException {

        String photoUrl = null;

        // Upload photo if provided
        if (photoFile != null && !photoFile.isEmpty()) {
            photoUrl = cloudinaryService.uploadVideo(photoFile); // implement uploadImage
        }

        InstructorDTO dto = new InstructorDTO();
        dto.setName(name);
        dto.setEmail(email);
        dto.setPhone(phone);
        dto.setCourseId(courseId);
        dto.setPhoto(photoUrl);

        InstructorDTO saved = instructorService.saveInstructor(dto);
        return ResponseEntity.ok(saved);
    }

    // Update Instructor (optional photo update)
    @PutMapping("/update/{id}")
    public ResponseEntity<InstructorDTO> updateInstructor(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam Long courseId,
            @RequestParam(value = "photo", required = false) MultipartFile photoFile
    ) throws IOException {

        String photoUrl = null;

        // Upload photo if provided
        if (photoFile != null && !photoFile.isEmpty()) {
            photoUrl = cloudinaryService.uploadVideo(photoFile);
        }

        InstructorDTO dto = new InstructorDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setEmail(email);
        dto.setPhone(phone);
        dto.setCourseId(courseId);
        dto.setPhoto(photoUrl); // keep null if not updated

        InstructorDTO updated = instructorService.updateInstructor(dto);
        return ResponseEntity.ok(updated);
        }


        @GetMapping("/getall")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<InstructorDTO>> getAllInstructors() {
        return ResponseEntity.ok(instructorService.getAllInstructors());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only admins should be able to update instructor details
    public ResponseEntity<InstructorDTO> updateInstructor(@PathVariable Long id, @RequestBody InstructorDTO instructorDTO) {
        // Pass the ID to ensure we're updating the correct instructor
        instructorDTO.setId(id); // Assuming your DTO has an ID field
        return ResponseEntity.ok(instructorService.updateInstructor(instructorDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only admins should be able to delete instructors
    public ResponseEntity<String> deleteInstructor(@PathVariable Long id) {
        instructorService.deleteInstructor(id);
        return ResponseEntity.ok("Instructor deleted successfully!");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<InstructorDTO> getInstructorById(@PathVariable Long id) {
        return ResponseEntity.ok(instructorService.getInstructorById(id));
    }

    // Additional endpoint for course-instructor relationship management
//    @PostMapping("/{instructorId}/courses/{courseId}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<String> assignCourse(@PathVariable Long instructorId, @PathVariable Long courseId) {
//        instructorService.assignCourseToInstructor(instructorId, courseId);
//        return ResponseEntity.ok("Course assigned to instructor successfully!");
//    }

//    @DeleteMapping("/{instructorId}/courses/{courseId}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<String> unassignCourse(@PathVariable Long instructorId, @PathVariable Long courseId) {
//        instructorService.unassignCourseFromInstructor(instructorId, courseId);
//        return ResponseEntity.ok("Course unassigned from instructor successfully!");
//    }
//
//    @GetMapping("/{instructorId}/courses")
//    @PreAuthorize("hasAnyRole('USER','ADMIN')")
//    public ResponseEntity<List<InstructorDTO>> getInstructorCourses(@PathVariable Long instructorId) {
//        return ResponseEntity.ok(instructorService.getInstructorCourses(instructorId));
//    }

    @GetMapping("/course/{courseId}")
    public List<Instructor> getInstructorsByCourseId(@PathVariable Long courseId) {
        return instructorService.getInstructorsByCourseId(courseId);
    }
}