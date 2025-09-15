//package lk.ijse.gdse72.backend.controller;
//
//
//import lk.ijse.gdse72.backend.dto.EnrollmentDTO;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/enrollments")
//@RequiredArgsConstructor
//@CrossOrigin("*")
//public class EnrollmentController {
//
//    private final EnrollmentService enrollmentService;
//
//    @PostMapping
//    @PreAuthorize("hasAnyRole('USER','ADMIN')")
//    public ResponseEntity<EnrollmentDTO> createEnrollment(@RequestBody EnrollmentDTO enrollmentDTO) {
//        return ResponseEntity.ok(enrollmentService.createEnrollment(enrollmentDTO));
//    }
//}