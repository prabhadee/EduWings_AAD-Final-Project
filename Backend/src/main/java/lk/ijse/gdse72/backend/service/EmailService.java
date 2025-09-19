package lk.ijse.gdse72.backend.service;

public interface EmailService {
    void sendPaymentSuccessEmail(String toEmail, String studentName, String paymentReference, Double amount);
}