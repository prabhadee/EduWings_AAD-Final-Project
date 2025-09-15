package lk.ijse.gdse72.backend.service.Impl;

import lk.ijse.gdse72.backend.dto.PaymentDTO;
import lk.ijse.gdse72.backend.entity.BatchMonth;
import lk.ijse.gdse72.backend.entity.Payment;
import lk.ijse.gdse72.backend.entity.User;
import lk.ijse.gdse72.backend.repository.BatchMonthRepository;
import lk.ijse.gdse72.backend.repository.PaymentRepository;
import lk.ijse.gdse72.backend.repository.UserRepository;
import lk.ijse.gdse72.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final BatchMonthRepository batchMonthRepository;

    @Value("${payhere.merchant-id}")
    private String merchantId;

    // ============================
    // 1. Create Payment (PENDING)
    // ============================
    @Override
    @Transactional
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        if (paymentDTO.getUserId() == null)
            throw new RuntimeException("User ID is required");

        if (paymentDTO.getAmount() == null || paymentDTO.getAmount() <= 0)
            throw new RuntimeException("Amount must be greater than zero");

        if (paymentDTO.getMonthIds() == null || paymentDTO.getMonthIds().isEmpty())
            throw new RuntimeException("At least one month must be selected");

        User user = userRepository.findById(paymentDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + paymentDTO.getUserId()));

        Set<BatchMonth> months = paymentDTO.getMonthIds().stream()
                .map(monthId -> batchMonthRepository.findById(monthId)
                        .orElseThrow(() -> new RuntimeException("Month not found with ID: " + monthId)))
                .collect(Collectors.toSet());

        Payment payment = Payment.builder()
                .user(user)
                .amount(paymentDTO.getAmount())
                .paymentDate(LocalDateTime.now())
                .status(Payment.PaymentStatus.PENDING)
                .referenceNumber(generateReferenceNumber())
                .months(months)
                .description(paymentDTO.getDescription())
                .build();

        Payment saved = paymentRepository.save(payment);
        return convertToDTO(saved);
    }

    // ============================
    // 2. Update Payment Status
    // ============================
    @Override
    @Transactional
    public PaymentDTO updatePaymentStatus(Long paymentId, Payment.PaymentStatus status) {
        if (paymentId == null)
            throw new RuntimeException("Payment ID is required for status update");

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        payment.setStatus(status);

        // Set payment date only when completed
        if (status == Payment.PaymentStatus.COMPLETED && payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }

        Payment saved = paymentRepository.save(payment);
        return convertToDTO(saved);
    }

    // ============================
    // 3. Get Payment by ID
    // ============================
    @Override
    public PaymentDTO getPaymentById(Long paymentId) {
        if (paymentId == null)
            throw new RuntimeException("Payment ID is required");

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        return convertToDTO(payment);
    }

    // ============================
    // 4. Get Payments by User
    // ============================
    @Override
    public java.util.List<PaymentDTO> getPaymentsByUser(Long userId) {
        if (userId == null)
            throw new RuntimeException("User ID is required");

        return paymentRepository.findByUser_Id(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ============================
    // 5. Get Payments by Month
    // ============================
    @Override
    public java.util.List<PaymentDTO> getPaymentsByMonth(Long monthId) {
        if (monthId == null)
            throw new RuntimeException("Month ID is required");

        return paymentRepository.findAll().stream()
                .filter(p -> p.getMonths().stream()
                        .anyMatch(m -> m.getMonthId().equals(monthId)))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ============================
    // 6. Get Payments by Status
    // ============================
    @Override
    public java.util.List<PaymentDTO> getPaymentsByStatus(Payment.PaymentStatus status) {
        if (status == null)
            throw new RuntimeException("Status is required");

        return paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == status)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ============================
    // 7. Delete Payment
    // ============================
    @Override
    @Transactional
    public void deletePayment(Long paymentId) {
        if (paymentId == null)
            throw new RuntimeException("Payment ID is required");

        if (!paymentRepository.existsById(paymentId))
            throw new RuntimeException("Payment not found with ID: " + paymentId);

        paymentRepository.deleteById(paymentId);
    }

    // ============================
    // 8. Get All Payments
    // ============================
    @Override
    public java.util.List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ============================
    // 9. Generate Payment Report
    // ============================
    @Override
    public String generatePaymentReport(Long paymentId) {
        if (paymentId == null)
            throw new RuntimeException("Payment ID is required");

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        StringBuilder sb = new StringBuilder();
        sb.append("Payment Report\n");
        sb.append("====================\n");
        sb.append("Reference Number: ").append(payment.getReferenceNumber()).append("\n");
        sb.append("Date: ").append(payment.getPaymentDate()).append("\n");
        sb.append("Amount: Rs. ").append(payment.getAmount()).append("\n");
        sb.append("Status: ").append(payment.getStatus()).append("\n");
        sb.append("User: ").append(payment.getUser().getUsername()).append(" (").append(payment.getUser().getEmail()).append(")\n");
        sb.append("Months Paid For:\n");

        payment.getMonths().forEach(m -> sb.append(" - ").append(m.getMonthName()).append("\n"));

        return sb.toString();
    }

    // ============================
    // Utility: Generate Ref #
    // ============================
    private String generateReferenceNumber() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // ============================
    // Utility: Convert to DTO
    // ============================
    private PaymentDTO convertToDTO(Payment payment) {
        return PaymentDTO.builder()
                .paymentId(payment.getPaymentId())
                .userId(payment.getUser().getId())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .status(payment.getStatus().name())
                .referenceNumber(payment.getReferenceNumber())
                .userEmail(payment.getUser().getEmail())
                .monthIds(payment.getMonths().stream()
                        .map(BatchMonth::getMonthId)
                        .collect(Collectors.toSet()))
                .description(payment.getDescription())
                .build();
    }
}
