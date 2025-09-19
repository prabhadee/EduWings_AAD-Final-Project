package lk.ijse.gdse72.backend.controller;

import lk.ijse.gdse72.backend.dto.PaymentDTO;
import lk.ijse.gdse72.backend.entity.Payment;
import lk.ijse.gdse72.backend.repository.PaymentRepository;
import lk.ijse.gdse72.backend.service.EmailService;
import lk.ijse.gdse72.backend.service.Impl.InvoicePDFService; // Fixed import path
import lk.ijse.gdse72.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Added logger import
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin("*")
@Slf4j // Added Slf4j annotation
public class PaymentController {

    // Use constructor injection consistently
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final InvoicePDFService invoicePDFService;
    private final EmailService emailService;

    @Value("${payhere.merchant-id}")
    private String merchantId;

    @Value("${payhere.merchant-secret}")
    private String merchantSecret;

    @Value("${app.base-url}")
    private String appBaseUrl;

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody PaymentDTO paymentDTO) {
        try {
            PaymentDTO created = paymentService.createPayment(paymentDTO);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error creating payment: " + e.getMessage()));
        }
    }

    @PostMapping("/create-payhere")
    public ResponseEntity<?> createPayHereForm(@RequestBody Map<String, Object> requestData) {
        try {
            Long userId = Long.parseLong(requestData.get("userId").toString());
            Set<Long> monthIds = Stream.of(requestData.get("monthIds").toString().split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());
            Double amount = Double.parseDouble(requestData.get("amount").toString());
            String description = requestData.get("description").toString();

            PaymentDTO paymentDTO = PaymentDTO.builder()
                    .userId(userId)
                    .amount(amount)
                    .currency("LKR")
                    .monthIds(monthIds)
                    .description(description)
                    .build();

            PaymentDTO createdPayment = paymentService.createPayment(paymentDTO);

            Map<String, Object> formData = paymentService.createPayHereFormData(
                    userId, monthIds, amount, description, createdPayment.getReferenceNumber());

            return ResponseEntity.ok(formData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create payment form", "message", e.getMessage()));
        }
    }

    @GetMapping("/success")
    public ResponseEntity<?> paymentSuccess(@RequestParam Map<String, String> params) {
        try {
            String orderId = params.get("order_id");
            if (orderId != null) {
                // Update payment status
                paymentService.updatePaymentStatusByReference(
                        orderId, Payment.PaymentStatus.COMPLETED);

                // Automatically send invoice email (async)
                CompletableFuture.runAsync(() -> {
                    try {
                        Payment payment = paymentRepository.findByReferenceNumber(orderId).orElse(null);
                        if (payment != null) {
                            emailService.sendPaymentSuccessEmail(
                                    payment.getUser().getEmail(),
                                    payment.getUser().getUsername(),
                                    payment.getReferenceNumber(),
                                    payment.getAmount()
                            );
                            log.info("Automatic invoice email sent for payment: {}", orderId);
                        }
                    } catch (Exception e) {
                        log.error("Failed to send automatic invoice email: {}", e.getMessage());
                    }
                });
            }

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", appBaseUrl + "/pages/student/payment-success.html?paymentId=" + orderId)
                    .build();
        } catch (Exception e) {
            log.error("Error processing payment success: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error processing payment");
        }
    }

    @GetMapping("/cancel")
    public ResponseEntity<?> paymentCancel(@RequestParam Map<String, String> params) {
        try {
            String orderId = params.get("order_id");
            if (orderId != null) {
                paymentService.updatePaymentStatusByReference(
                        orderId, Payment.PaymentStatus.FAILED);
            }
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", appBaseUrl + "/pages/student/payment-cancel.html?paymentId=" + orderId)
                    .build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error cancelling payment");
        }
    }

    @PostMapping("/notify")
    public ResponseEntity<String> paymentNotify(@RequestParam Map<String, String> params) {
        try {
            log.info("PayHere Notify received: {}", params);
            if (!verifyPayHereSignature(params)) {
                log.warn("Invalid PayHere signature");
                return ResponseEntity.badRequest().body("Invalid signature");
            }
            paymentService.updatePaymentFromPayHere(params);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Error handling PayHere notification: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error handling notification");
        }
    }

    private boolean verifyPayHereSignature(Map<String, String> params) {
        try {
            String[] keys = {"merchant_id", "order_id", "payhere_amount", "payhere_currency", "status_code"};
            StringBuilder signatureData = new StringBuilder();
            for (String key : keys) {
                signatureData.append(params.getOrDefault(key, ""));
            }
            signatureData.append(merchantSecret);
            String generatedSignature = DigestUtils.md5DigestAsHex(signatureData.toString().getBytes()).toUpperCase();
            String receivedSignature = params.get("md5sig");
            return generatedSignature.equalsIgnoreCase(receivedSignature);
        } catch (Exception e) {
            log.error("Error verifying PayHere signature: {}", e.getMessage());
            return false;
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByUser(@PathVariable("userId") Long userId) {
        List<PaymentDTO> payments = paymentService.getPaymentsByUser(userId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        List<PaymentDTO> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/invoice/{paymentReference}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable String paymentReference) {
        try {
            byte[] pdfBytes = invoicePDFService.generateInvoicePDF(paymentReference);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=Invoice_" + paymentReference + ".pdf")
                    .body(pdfBytes);

        } catch (Exception e) {
            log.error("Error generating invoice PDF for payment {}: {}", paymentReference, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating invoice: " + e.getMessage()).getBytes());
        }
    }

    @PostMapping("/send-invoice-email/{paymentReference}")
    public ResponseEntity<?> sendInvoiceEmail(@PathVariable String paymentReference) {
        try {
            // Get payment details
            Payment payment = paymentRepository.findByReferenceNumber(paymentReference)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            // Send email with invoice
            emailService.sendPaymentSuccessEmail(
                    payment.getUser().getEmail(),
                    payment.getUser().getUsername(),
                    payment.getReferenceNumber(),
                    payment.getAmount()
            );

            return ResponseEntity.ok(Map.of("message", "Invoice email sent successfully"));

        } catch (Exception e) {
            log.error("Error sending invoice email for payment {}: {}", paymentReference, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to send invoice email: " + e.getMessage()));
        }
    }

    @GetMapping("/status/{paymentReference}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String paymentReference) {
        try {
            Payment payment = paymentRepository.findByReferenceNumber(paymentReference)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            return ResponseEntity.ok(Map.of(
                    "reference", payment.getReferenceNumber(),
                    "status", payment.getStatus().toString(),
                    "amount", payment.getAmount(),
                    "date", payment.getPaymentDate()
            ));

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}