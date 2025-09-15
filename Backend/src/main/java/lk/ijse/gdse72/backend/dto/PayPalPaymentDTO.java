package lk.ijse.gdse72.backend.dto;

import lombok.Data;

@Data
public class PayPalPaymentDTO {
    private String amount;
    private String currency;
    private String description;
}