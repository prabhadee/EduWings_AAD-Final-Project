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
import lombok.extern.slf4j.Slf4j;
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
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final BatchMonthRepository batchMonthRepository;

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
        if (paymentDTO.getUserId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");

        if (paymentDTO.getAmount() == null || paymentDTO.getAmount() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be greater than zero");

        if (paymentDTO.getCurrency() == null || paymentDTO.getCurrency().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Currency is required");

        if (paymentDTO.getMonthIds() == null || paymentDTO.getMonthIds().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one month must be selected");

        User user = userRepository.findById(paymentDTO.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with ID: " + paymentDTO.getUserId()));

        Set<BatchMonth> months = paymentDTO.getMonthIds().stream()
                .map(monthId -> batchMonthRepository.findById(monthId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Month not found with ID: " + monthId)))
                .collect(Collectors.toSet());

        Payment payment = Payment.builder()
                .user(user)
                .amount(paymentDTO.getAmount())
                .currency(paymentDTO.getCurrency())
                .paymentDate(LocalDateTime.now())
                .status(Payment.PaymentStatus.PENDING) // initial status
                .referenceNumber(generateReferenceNumber())
                .months(months)
                .description(paymentDTO.getDescription())
                .build();

        payment = paymentRepository.save(payment);
        return convertToDTO(payment);
    }

    @Override
    public Map<String, Object> createPayHereFormData(Long userId, Set<Long> monthIds, Double amount, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String orderId = "EDU-" + UUID.randomUUID().toString().substring(0, 8);
        String currency = "LKR";
        String formattedAmount = String.format("%.2f", amount);
        String hash = generatePayHereHash(merchantId, orderId, formattedAmount, currency, merchantSecret);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("merchant_id", merchantId);
        data.put("return_url", appBaseUrl + "/api/payments/success");
        data.put("cancel_url", appBaseUrl + "/api/payments/cancel");
        data.put("notify_url", appBaseUrl + "/api/payments/notify");
        data.put("order_id", orderId);
        data.put("items", description);
        data.put("amount", formattedAmount);
        data.put("currency", currency);
        data.put("hash", hash);

        String userName = user.getUsername() != null ? user.getUsername() : "User";

        data.put("first_name", "firstName");
        data.put("last_name", "lastName");
        data.put("user_name", userName);
        data.put("email", user.getEmail() != null ? user.getEmail() : "student@eduwings.com");
        data.put("phone", user.getNumber() != null ? user.getNumber() : "0771234567");
        data.put("address", "EduWings Institute");
        data.put("city", "Colombo");
        data.put("country", "Sri Lanka");
        data.put("custom_1", monthIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
        data.put("custom_2", userId.toString());
        data.put("sandbox", "1");

        return data;
    }

    @Override
    @Transactional
    public void updatePaymentFromPayHere(Map<String, String> params) {
        String status = params.get("status_code");
        String custom1 = params.get("custom_1");
        String custom2 = params.get("custom_2");

        // Parse custom data
        Set<Long> monthIds = parseMonthIds(custom1);
        Long userId = Long.parseLong(custom2);

        if ("2".equals(status)) { // PayHere Completed
            PaymentDTO paymentDTO = PaymentDTO.builder()
                    .userId(userId)
                    .amount(Double.parseDouble(params.get("payhere_amount")))
                    .currency(params.get("payhere_currency"))
                    .monthIds(monthIds)
                    .description(params.get("items"))
                    .build();

            // Save as COMPLETED
            PaymentDTO saved = createPayment(paymentDTO);
            updatePaymentStatus(saved.getPaymentId(), Payment.PaymentStatus.COMPLETED);
        }
    }

    private Set<Long> parseMonthIds(String customData) {
        return Stream.of(customData.split(","))
                .filter(s -> !s.isBlank())
                .map(Long::parseLong)
                .collect(Collectors.toSet());
    }

    private String generatePayHereHash(String merchantId, String orderId, String amount, String currency, String merchantSecret) {
        try {
            String secretMd5 = md5Hex(merchantSecret).toUpperCase();
            return md5Hex(merchantId + orderId + amount + currency + secretMd5).toUpperCase();
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
        if (paymentId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment ID is required for status update");

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Payment not found with ID: " + paymentId));

        payment.setStatus(status);

        if (status == Payment.PaymentStatus.COMPLETED && payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }

        Payment saved = paymentRepository.save(payment);
        return convertToDTO(saved);
    }

    @Override
    public PaymentDTO getPaymentById(Long paymentId) {
        if (paymentId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment ID is required");

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Payment not found with ID: " + paymentId));

        return convertToDTO(payment);
    }

    @Override
    public List<PaymentDTO> getPaymentsByUser(Long userId) {
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");

        return paymentRepository.findByUser_Id(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDTO> getPaymentsByMonth(Long monthId) {
        if (monthId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Month ID is required");

        return paymentRepository.findAll().stream()
                .filter(p -> p.getMonths().stream()
                        .anyMatch(m -> m.getMonthId().equals(monthId)))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDTO> getPaymentsByStatus(Payment.PaymentStatus status) {
        if (status == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");

        return paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == status)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePayment(Long paymentId) {
        if (paymentId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment ID is required");

        if (!paymentRepository.existsById(paymentId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Payment not found with ID: " + paymentId);

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
        if (paymentId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment ID is required");

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Payment not found with ID: " + paymentId));

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
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
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
}
