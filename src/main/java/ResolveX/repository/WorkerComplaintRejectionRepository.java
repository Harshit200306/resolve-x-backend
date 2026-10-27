package ResolveX.repository;

import ResolveX.entity.WorkerComplaintRejection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkerComplaintRejectionRepository
        extends JpaRepository<WorkerComplaintRejection, Long> {

    List<WorkerComplaintRejection> findByWorker_Id(Long workerId);

    boolean existsByComplaint_IdAndWorker_Id(
            Long complaintId,
            Long workerId
    );
}