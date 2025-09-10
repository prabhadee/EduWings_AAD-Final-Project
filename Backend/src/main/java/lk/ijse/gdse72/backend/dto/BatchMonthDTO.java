package lk.ijse.gdse72.backend.dto;

import lombok.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchMonthDTO {
    private Long monthId;
    private String monthName;
    private Long batchId;
    private Set<Long> moduleIds; // IDs of videos in this month
}
