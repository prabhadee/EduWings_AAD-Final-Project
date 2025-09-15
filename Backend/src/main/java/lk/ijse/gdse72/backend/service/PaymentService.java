package lk.ijse.gdse72.backend.service;

import lk.ijse.gdse72.backend.dto.PaymentDTO;
import lk.ijse.gdse72.backend.entity.Payment;

import java.util.List;

public interface PaymentService {

    /**
     * Create a new payment (initially PENDING), with associated months.
     * @param paymentDTO DTO containing amount, userId, monthIds, description, userEmail (others generated).
     * @return the created PaymentDTO (with paymentId, referenceNumber, status, etc).
     * @throws RuntimeException if user or months not found, or invalid data.
     */
    PaymentDTO createPayment(PaymentDTO paymentDTO);

    /**
     * Update a payment's status.
     * @param paymentId the ID of the payment.
     * @param status the new status enum.
     * @return updated PaymentDTO.
     */
    PaymentDTO updatePaymentStatus(Long paymentId, Payment.PaymentStatus status);

    PaymentDTO getPaymentById(Long paymentId);

    List<PaymentDTO> getPaymentsByUser(Long userId);

    List<PaymentDTO> getPaymentsByMonth(Long monthId);

    List<PaymentDTO> getPaymentsByStatus(Payment.PaymentStatus status);

    void deletePayment(Long paymentId);

    List<PaymentDTO> getAllPayments();

    /**
     * Generate a simple textual receipt / report for the payment.
     * @param paymentId id of payment
     * @return report string
     */
    String generatePaymentReport(Long paymentId);
}
