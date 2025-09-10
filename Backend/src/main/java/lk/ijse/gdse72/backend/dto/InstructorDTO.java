package lk.ijse.gdse72.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String photo;
    private Long courseId; // map the course ID
}
