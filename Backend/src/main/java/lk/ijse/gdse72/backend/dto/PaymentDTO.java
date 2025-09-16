package lk.ijse.gdse72.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class PaymentDTO {
    private Long paymentId;               // generated id
    private Long userId;                  // required
    private Double amount;                // required, > 0
    private String currency;              // required, e.g., "LKR"
    private LocalDateTime paymentDate;    // may be returned, not required from frontend
    private String status;                // or enum name
    private String referenceNumber;
    private String userEmail;             // optional
    private Set<Long> monthIds;           // required: at least one
    private String description;           // optional
}