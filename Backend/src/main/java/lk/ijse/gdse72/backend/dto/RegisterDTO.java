package lk.ijse.gdse72.backend.dto;

import lk.ijse.gdse72.backend.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDTO {
    private String username;
    private String email;
    private String number;
    private String password;
    private String role; //USER or ADMIN
}
