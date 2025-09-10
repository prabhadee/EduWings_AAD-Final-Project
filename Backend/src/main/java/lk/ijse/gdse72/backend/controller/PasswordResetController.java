package lk.ijse.gdse72.backend.controller;

import lk.ijse.gdse72.backend.dto.OtpVerifyDTO;
import lk.ijse.gdse72.backend.dto.PasswordResetDTO;
import lk.ijse.gdse72.backend.dto.PasswordResetRequestDTO;
import lk.ijse.gdse72.backend.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PasswordResetController {

    private final PasswordResetService resetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody PasswordResetRequestDTO request) {
        try {
            resetService.generateOtp(request.getEmail());
            return ResponseEntity.ok("OTP sent to email");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerifyDTO request) {
        try {
            boolean valid = resetService.verifyOtp(request.getEmail(), request.getOtp());
            return valid ? ResponseEntity.ok("OTP verified") : ResponseEntity.badRequest().body("Invalid or expired OTP");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetDTO request) {
        try {
            boolean valid = resetService.verifyOtp(request.getEmail(), request.getOtp());
            if (!valid) {
                return ResponseEntity.badRequest().body("Invalid or expired OTP");
            }

            resetService.resetPassword(request.getEmail(), request.getNewPassword());
            return ResponseEntity.ok("Password reset successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}