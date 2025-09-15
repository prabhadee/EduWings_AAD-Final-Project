package lk.ijse.gdse72.backend.repository;

import lk.ijse.gdse72.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Payments by user
    List<Payment> findByUser_Id(Long userId);

    // Payments by month (fix for ManyToMany)
    @Query("SELECT DISTINCT p FROM Payment p LEFT JOIN FETCH p.months m WHERE m.monthId = :monthId")
    List<Payment> findByMonthId(@Param("monthId") Long monthId);

    // Payments by status
    List<Payment> findByStatus(Payment.PaymentStatus status);

    // Lookup by reference number
    Optional<Payment> findByReferenceNumber(String referenceNumber);
}
