package lk.ijse.gdse72.backend.service.Impl;

import lk.ijse.gdse72.backend.entity.PasswordResetToken;
import lk.ijse.gdse72.backend.entity.User;
import lk.ijse.gdse72.backend.repository.PasswordResetTokenRepository;
import lk.ijse.gdse72.backend.repository.UserRepository;
import lk.ijse.gdse72.backend.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetTokenRepository tokenRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Override
    public void generateOtp(String email) {
        // Clean up any existing tokens for this email
        tokenRepo.deleteByEmail(email);

        String otp = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);

        PasswordResetToken token = new PasswordResetToken(email, otp, expiry);
        tokenRepo.save(token);

        // Send OTP to email
        sendOtpEmail(email, otp);
    }

    private void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Password Reset OTP - EduWings");
            message.setText("Your OTP for password reset is: " + otp + "\nThis OTP will expire in 5 minutes.");
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        Optional<PasswordResetToken> tokenOpt = tokenRepo.findByEmailAndOtp(email, otp);

        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken token = tokenOpt.get();

        // Check if OTP is expired
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            tokenRepo.delete(token); // Clean up expired token
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public void resetPassword(String email, String newPassword) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        // Clean up the token
        tokenRepo.deleteByEmail(email);
    }
}