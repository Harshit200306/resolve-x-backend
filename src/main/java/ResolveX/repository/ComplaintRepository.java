package ResolveX.repository;

import ResolveX.entity.Complaint;
import ResolveX.entity.ComplaintStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByUserId(Long userId);

    List<Complaint> findByWorker_Id(Long workerId);

    List<Complaint> findByWorkerIsNullAndStatusAndCategory(
            ComplaintStatus status,
            String category
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Complaint c WHERE c.id = :complaintId")
    Optional<Complaint> findByIdForUpdate(
            @Param("complaintId") Long complaintId
    );
}

