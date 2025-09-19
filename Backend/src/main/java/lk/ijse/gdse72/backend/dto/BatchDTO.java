package lk.ijse.gdse72.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchDTO {
    private Long batchId;
    private String batchName;
    private Double monthlyFee;
    private Long courseId;
    private Long instructorId;
    private Set<Long> monthIds;



}
