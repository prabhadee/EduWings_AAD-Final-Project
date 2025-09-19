package lk.ijse.gdse72.backend.service.Impl;

import lk.ijse.gdse72.backend.dto.PaymentDTO;
import lk.ijse.gdse72.backend.entity.BatchMonth;
import lk.ijse.gdse72.backend.entity.Payment;
import lk.ijse.gdse72.backend.entity.User;
import lk.ijse.gdse72.backend.repository.BatchMonthRepository;
import lk.ijse.gdse72.backend.repository.PaymentRepository;
import lk.ijse.gdse72.backend.repository.UserRepository;
import lk.ijse.gdse72.backend.service.EmailService;
import lk.ijse.gdse72.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final BatchMonthRepository batchMonthRepository;
    private final ModelMapper modelMapper;
    private final EmailService emailService;

    @Value("${payhere.merchant-id}")
    private String merchantId;

    @Value("${payhere.merchant-secret}")
    private String merchantSecret;

    @Value("${payhere.base-url}")
    private String payhereBaseUrl;

    @Value("${app.base-url}")
    private String appBaseUrl;

    @Override
    @Transactional
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        log.info("Creating payment for user: {}", paymentDTO.getUserId());
        if (paymentDTO.getUserId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        if (paymentDTO.getAmount() == null || paymentDTO.getAmount() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be greater than zero");
        if (paymentDTO.getCurrency() == null || paymentDTO.getCurrency().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Currency is required");
        if (paymentDTO.getMonthIds() == null || paymentDTO.getMonthIds().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one month must be selected");

        User user = userRepository.findById(paymentDTO.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + paymentDTO.getUserId()));

        Set<BatchMonth> months = paymentDTO.getMonthIds().stream()
                .map(monthId -> batchMonthRepository.findById(monthId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Month not found with ID: " + monthId)))
                .collect(Collectors.toSet());

        Payment payment = Payment.builder()
                .user(user)
                .amount(paymentDTO.getAmount())
                .currency(paymentDTO.getCurrency())
                .paymentDate(LocalDateTime.now())
                .status(Payment.PaymentStatus.PENDING)
                .referenceNumber(generateReferenceNumber())
                .months(months)
                .description(paymentDTO.getDescription())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created with ID: {} and reference: {}", savedPayment.getPaymentId(), savedPayment.getReferenceNumber());
        return convertToDTO(savedPayment);
    }

    @Override
    public Map<String, Object> createPayHereFormData(Long userId, Set<Long> monthIds, Double amount, String description, String referenceNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String currency = "LKR";
        String formattedAmount = String.format("%.2f", amount);
        String hash = generatePayHereHash(merchantId, referenceNumber, formattedAmount, currency, merchantSecret);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("merchant_id", merchantId);
        data.put("return_url", appBaseUrl + "/api/payments/success");
        data.put("cancel_url", appBaseUrl + "/api/payments/cancel");
        data.put("notify_url", appBaseUrl + "/api/payments/notify");
        data.put("order_id", referenceNumber);
        data.put("items", description);
        data.put("amount", formattedAmount);
        data.put("currency", currency);
        data.put("hash", hash);

        // User information
        data.put("first_name", user.getUsername() != null ? user.getUsername() : "Student");
        data.put("last_name", "User");
        data.put("email", user.getEmail() != null ? user.getEmail() : "student@eduwings.com");
        data.put("phone", user.getNumber() != null ? user.getNumber() : "0771234567");
        data.put("address", "EduWings Institute");
        data.put("city", "Colombo");
        data.put("country", "Sri Lanka");

        // Custom data for tracking
        data.put("custom_1", monthIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
        data.put("custom_2", userId.toString());
        data.put("sandbox", true);

        return data;
    }

    @Override
    @Transactional
    public void updatePaymentFromPayHere(Map<String, String> params) {
        log.info("Updating payment from PayHere notification: {}", params);
        String orderId = params.get("order_id");
        String statusCode = params.get("status_code");
        if (orderId == null) {
            log.error("No order_id in PayHere notification");
            return;
        }

        Optional<Payment> paymentOpt = paymentRepository.findByReferenceNumber(orderId);
        if (paymentOpt.isEmpty()) {
            log.error("Payment not found for reference number: {}", orderId);
            return;
        }

        Payment payment = paymentOpt.get();

        // Update status based on PayHere status code
        Payment.PaymentStatus newStatus = Payment.PaymentStatus.PENDING;
        switch (statusCode) {
            case "2": // Completed
                newStatus = Payment.PaymentStatus.COMPLETED;
                payment.setPaymentDate(LocalDateTime.now());

                // Save payment first
                paymentRepository.save(payment);

                // Send email asynchronously
                try {
                    User user = payment.getUser();
                    emailService.sendPaymentSuccessEmail(user.getEmail(), user.getUsername(), payment.getReferenceNumber(), payment.getAmount());
                    log.info("Payment success email sent to {}", user.getEmail());
                } catch (Exception ex) {
                    log.error("Failed to send payment success email: {}", ex.getMessage());
                }
                break;
            case "0": // Pending
                newStatus = Payment.PaymentStatus.PENDING;
                break;
            case "-1":
            case "-2":
            case "-3": // Failed
                newStatus = Payment.PaymentStatus.FAILED;
                break;
            default:
                log.warn("Unknown PayHere status code: {}", statusCode);
                newStatus = Payment.PaymentStatus.FAILED;
        }

        payment.setStatus(newStatus);
        paymentRepository.save(payment);
        log.info("Payment {} updated to status: {}", orderId, newStatus);
    }

    private String generatePayHereHash(String merchantId, String orderId, String amount, String currency, String merchantSecret) {
        try {
            String secretMd5 = md5Hex(merchantSecret).toUpperCase();
            String data = merchantId + orderId + amount + currency + secretMd5;
            return md5Hex(data).toUpperCase();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating hash");
        }
    }

    private String md5Hex(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    @Override
    @Transactional
    public PaymentDTO updatePaymentStatus(Long paymentId, Payment.PaymentStatus status) {
        if (paymentId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment ID is required for status update");

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found with ID: " + paymentId));

        payment.setStatus(status);
        if (status == Payment.PaymentStatus.COMPLETED && payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }

        Payment saved = paymentRepository.save(payment);
        return convertToDTO(saved);
    }

    @Override
    @Transactional
    public PaymentDTO updatePaymentStatusByReference(String referenceNumber, Payment.PaymentStatus status) {
        if (referenceNumber == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reference number is required");

        Payment payment = paymentRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found with reference: " + referenceNumber));

        payment.setStatus(status);
        if (status == Payment.PaymentStatus.COMPLETED) {
            payment.setPaymentDate(LocalDateTime.now());
        }

        Payment saved = paymentRepository.save(payment);
        return convertToDTO(saved);
    }

    @Override
    public PaymentDTO getPaymentById(Long paymentId) {
        if (paymentId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment ID is required");

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found with ID: " + paymentId));

        return convertToDTO(payment);
    }

    @Override
    public List<PaymentDTO> getPaymentsByMonth(Long monthId) {
        if (monthId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Month ID is required");

        return paymentRepository.findByMonthId(monthId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDTO> getPaymentsByStatus(Payment.PaymentStatus status) {
        if (status == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");

        return paymentRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePayment(Long paymentId) {
        if (paymentId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment ID is required");
        if (!paymentRepository.existsById(paymentId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found with ID: " + paymentId);

        paymentRepository.deleteById(paymentId);
    }

    @Override
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String generatePaymentReport(Long paymentId) {
        if (paymentId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment ID is required");

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found with ID: " + paymentId));

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

    private String generateReferenceNumber() {
        return "PAY-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private PaymentDTO convertToDTO(Payment payment) {
        return PaymentDTO.builder()
                .paymentId(payment.getPaymentId())
                .userId(payment.getUser().getId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
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

    public List<PaymentDTO> getPaymentsByUser(Long userId) {
        List<Payment> payments = paymentRepository.findByUser_Id(userId);
        return payments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
