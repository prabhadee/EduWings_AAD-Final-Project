package lk.ijse.gdse72.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String otp;

    private LocalDateTime expiresAt;

    private int attempts = 0;

    public PasswordResetToken(String email, String otp, LocalDateTime expiresAt) {
        this.email = email;
        this.otp = otp;
        this.expiresAt = expiresAt;
    }
}