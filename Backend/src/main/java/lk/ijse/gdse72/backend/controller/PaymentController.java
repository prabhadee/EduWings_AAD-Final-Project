package lk.ijse.gdse72.backend.controller;

import lk.ijse.gdse72.backend.dto.PaymentDTO;
import lk.ijse.gdse72.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                paymentService.updatePaymentStatusByReference(
                        orderId, lk.ijse.gdse72.backend.entity.Payment.PaymentStatus.COMPLETED);
            }
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", appBaseUrl + "/pages/student/payment-success.html?paymentId=" + orderId)
                    .build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing payment");
        }
    }

    @GetMapping("/cancel")
    public ResponseEntity<?> paymentCancel(@RequestParam Map<String, String> params) {
        try {
            String orderId = params.get("order_id");
            if (orderId != null) {
                paymentService.updatePaymentStatusByReference(
                        orderId, lk.ijse.gdse72.backend.entity.Payment.PaymentStatus.FAILED);
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
            System.out.println("PayHere Notify received: " + params);
            if (!verifyPayHereSignature(params)) {
                System.out.println("Invalid signature");
                return ResponseEntity.badRequest().body("Invalid signature");
            }
            paymentService.updatePaymentFromPayHere(params);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            System.out.println("Error handling notification: " + e.getMessage());
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
}
