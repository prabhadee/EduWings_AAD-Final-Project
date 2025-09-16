package lk.ijse.gdse72.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "batch_month")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchMonth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long monthId;

    @Column(nullable = false)
    private String monthName; // Example: January, February

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @OneToMany(mappedBy = "month", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoModule> modules = new HashSet<>();

    @ManyToMany(mappedBy = "months")
    private Set<Payment> payments = new HashSet<>();

//
}
