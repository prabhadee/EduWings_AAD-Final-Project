package lk.ijse.gdse72.backend.service;

import lk.ijse.gdse72.backend.dto.PaymentDTO;
import lk.ijse.gdse72.backend.entity.Payment;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PaymentService {

    PaymentDTO createPayment(PaymentDTO paymentDTO);

    PaymentDTO updatePaymentStatus(Long paymentId, Payment.PaymentStatus status);

    PaymentDTO getPaymentById(Long paymentId);

    List<PaymentDTO> getPaymentsByUser(Long userId);

    List<PaymentDTO> getPaymentsByMonth(Long monthId);

    List<PaymentDTO> getPaymentsByStatus(Payment.PaymentStatus status);

    void deletePayment(Long paymentId);

    List<PaymentDTO> getAllPayments();

    String generatePaymentReport(Long paymentId);

    // New methods for PayHere integration
    Map<String, Object> createPayHereFormData(Long userId, Set<Long> monthIds, Double amount, String description);

    void updatePaymentFromPayHere(Map<String, String> params);
}