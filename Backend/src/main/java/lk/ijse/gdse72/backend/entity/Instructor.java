package lk.ijse.gdse72.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "instructors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Auto increment ID

    @NotBlank(message = "Instructor name cannot be empty")
    @Pattern(
            regexp = "^[a-zA-Z ]+$",
            message = "Name can only contain letters and spaces"
    )
    @Column(nullable = false)
    private String name;

//    @NotBlank(message = "Email cannot be empty")
//    @Email(message = "Invalid email format")
//    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(
            regexp = "^(?:\\+94|0)[1-9][0-9]{8}$",
            message = "Phone number must be valid (e.g., +94771234567 or 0771234567)"
    )
    @Column(nullable = false)
    private String phone;

    private String photo; // Optional → no validation

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // Many instructors → One course
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @JsonBackReference
    private Course course;
}
