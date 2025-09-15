package lk.ijse.gdse72.backend.controller;

import lk.ijse.gdse72.backend.dto.PaymentDTO;
import lk.ijse.gdse72.backend.entity.Payment;
import lk.ijse.gdse72.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${payhere.merchant-id}")
    private String merchantId;

    @Value("${payhere.merchant-secret}")
    private String merchantSecret;

    // ==============================
    // 1. Create Payment
    // ==============================
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody PaymentDTO paymentDTO) {
        try {
            if (paymentDTO.getAmount() == null || paymentDTO.getAmount() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Amount must be positive"));
            }
            if (paymentDTO.getUserId() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "User ID is required"));
            }
            if (paymentDTO.getMonthIds() == null || paymentDTO.getMonthIds().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "At least one month must be selected"));
            }

            PaymentDTO created = paymentService.createPayment(paymentDTO);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Error creating payment: " + e.getMessage()));
        }
    }

    // ==============================
    // 2. Update Payment Status (Manual)
    // ==============================
    @PatchMapping("/{paymentId}/status")
    public ResponseEntity<PaymentDTO> updatePaymentStatus(
            @PathVariable Long paymentId,
            @RequestParam Payment.PaymentStatus status) {
        try {
            PaymentDTO updated = paymentService.updatePaymentStatus(paymentId, status);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // ==============================
    // 3. Get Payment by ID
    // ==============================
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long paymentId) {
        try {
            PaymentDTO payment = paymentService.getPaymentById(paymentId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    // ==============================
    // 4. Get Payments by User
    // ==============================
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPaymentsByUser(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(paymentService.getPaymentsByUser(userId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // ==============================
    // 5. Payment Success Callback
    // ==============================
    @PostMapping("/success")
    public ResponseEntity<?> paymentSuccess(@RequestParam Map<String, String> params) {
        try {
            if (!verifyPayHereSignature(params)) {
                return ResponseEntity.badRequest().body("Invalid signature");
            }

            String orderId = params.get("order_id");
            String statusCode = params.get("status_code");

            if ("2".equals(statusCode)) { // PayHere Completed
                paymentService.updatePaymentStatus(
                        Long.parseLong(orderId),
                        Payment.PaymentStatus.COMPLETED
                );

                return ResponseEntity.status(HttpStatus.FOUND)
                        .header("Location", "http://localhost:8080/payment-success.html?paymentId=" + orderId)
                        .build();
            }

            return ResponseEntity.ok("Payment not completed");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error processing payment");
        }
    }

    // ==============================
    // 6. Payment Cancel Callback
    // ==============================
    @PostMapping("/cancel")
    public ResponseEntity<?> paymentCancel(@RequestParam Map<String, String> params) {
        try {
            String orderId = params.get("order_id");
            paymentService.updatePaymentStatus(
                    Long.parseLong(orderId),
                    Payment.PaymentStatus.FAILED
            );

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "http://localhost:8080/payment-cancel.html?paymentId=" + orderId)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error cancelling payment");
        }
    }

    // ==============================
    // 7. Notify (Server-to-Server)
    // ==============================
    @PostMapping("/notify")
    public ResponseEntity<String> paymentNotify(@RequestParam Map<String, String> params) {
        System.out.println("Notification from PayHere: " + params);

        try {
            if (!verifyPayHereSignature(params)) {
                return ResponseEntity.badRequest().body("Invalid signature");
            }

            String orderId = params.get("order_id");
            String statusCode = params.get("status_code");

            if ("2".equals(statusCode)) {
                paymentService.updatePaymentStatus(
                        Long.parseLong(orderId),
                        Payment.PaymentStatus.COMPLETED
                );
            } else {
                paymentService.updatePaymentStatus(
                        Long.parseLong(orderId),
                        Payment.PaymentStatus.FAILED
                );
            }

            return ResponseEntity.ok("Notification processed");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error handling notification");
        }
    }

    // ==============================
    // Signature Verification Method
    // ==============================
    private boolean verifyPayHereSignature(Map<String, String> params) {
        try {
            String[] keys = {"merchant_id", "order_id", "payhere_amount", "payhere_currency", "status_code"};
            StringBuilder signatureData = new StringBuilder();

            for (String key : keys) {
                signatureData.append(params.getOrDefault(key, "")).append("");
            }

            signatureData.append(merchantSecret);

            String generatedSignature = DigestUtils.md5DigestAsHex(signatureData.toString().getBytes()).toUpperCase();
            String receivedSignature = params.get("md5sig");

            return generatedSignature.equalsIgnoreCase(receivedSignature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
