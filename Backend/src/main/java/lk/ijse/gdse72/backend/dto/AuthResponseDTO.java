package lk.ijse.gdse72.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private String accessToken;
    private String role;
    private String username;
    private Long userId;
}