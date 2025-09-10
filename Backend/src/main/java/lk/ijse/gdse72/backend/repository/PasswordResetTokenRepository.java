package lk.ijse.gdse72.backend.repository;

import lk.ijse.gdse72.backend.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByEmail(String email);
    Optional<PasswordResetToken> findByEmailAndOtp(String email, String otp);

    // Add this method to delete by email
    @Transactional
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.email = :email")
    void deleteByEmail(String email);
}