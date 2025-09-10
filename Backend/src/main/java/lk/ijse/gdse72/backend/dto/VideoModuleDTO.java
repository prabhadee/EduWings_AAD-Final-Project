package lk.ijse.gdse72.backend.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoModuleDTO {
    private Long moduleId;
    private String title;
    private List<String> videoUrls;  // Changed from single videoUrl to list
    private Long monthId;
}