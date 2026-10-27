package ResolveX.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "worker_complaint_rejections",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"complaint_id", "worker_id"}
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkerComplaintRejection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint;

    @ManyToOne(optional = false)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    private LocalDateTime rejectedAt;

    public WorkerComplaintRejection(
            Complaint complaint,
            Worker worker,
            LocalDateTime rejectedAt
    ) {
        this.complaint = complaint;
        this.worker = worker;
        this.rejectedAt = rejectedAt;
    }
}