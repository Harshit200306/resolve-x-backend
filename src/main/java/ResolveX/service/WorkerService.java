package ResolveX.service;

import ResolveX.dto.ComplaintResponse;
import ResolveX.dto.WorkerResponse;
import ResolveX.entity.Complaint;
import ResolveX.entity.ComplaintStatus;
import ResolveX.entity.Worker;
import ResolveX.repository.ComplaintRepository;
import ResolveX.repository.WorkerRepository;
import ResolveX.entity.WorkerComplaintRejection;
import ResolveX.repository.WorkerComplaintRejectionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class WorkerService {
    private final WorkerRepository workerRepository;
    private final ComplaintRepository complaintRepository;
    private final WorkerComplaintRejectionRepository rejectionRepository;

    public List<WorkerResponse> getAvailableWorkers() {
        return workerRepository.findByAvailableTrue()
                .stream()
                .map(worker -> new WorkerResponse(
                        worker.getId(),
                        worker.getUser().getName(),
                        worker.getCategory(),
                        worker.getExperience(),
                        worker.getAvailable()
                ))
                .toList();
    }

    public List<ComplaintResponse> getAvailableComplaints(String email) {
        Worker worker = workerRepository.findByUserEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Worker profile not found")
                );

        if (!Boolean.TRUE.equals(worker.getAvailable())) {
            return List.of();
        }

        List<Complaint> complaints =
                complaintRepository.findByWorkerIsNullAndStatusAndCategory(
                        ComplaintStatus.PENDING,
                        worker.getCategory()
                );

        List<Long> rejectedComplaintIds =
                rejectionRepository.findByWorker_Id(worker.getId())
                        .stream()
                        .map(rejection -> rejection.getComplaint().getId())
                        .toList();

        return complaints.stream()
                .filter(complaint ->
                        !rejectedComplaintIds.contains(complaint.getId())
                )
                .map(complaint -> convertToResponse(complaint, false))
                .toList();
    }

    public List<ComplaintResponse> getMyAssignedComplaints(String email) {
        Worker worker = workerRepository.findByUserEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Worker profile not found")
                );

        return complaintRepository.findByWorker_Id(worker.getId())
                .stream()
                .map(complaint -> convertToResponse(complaint, true))
                .toList();
    }

    public WorkerResponse updateAvailability(String email, Boolean available) {
        Worker worker = workerRepository.findByUserEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Worker profile not found")
                );

        worker.setAvailable(available);

        Worker savedWorker = workerRepository.save(worker);

        return new WorkerResponse(
                savedWorker.getId(),
                savedWorker.getUser().getName(),
                savedWorker.getCategory(),
                savedWorker.getExperience(),
                savedWorker.getAvailable()
        );
    }

    public WorkerResponse getMyProfile(String email) {
        Worker worker = workerRepository.findByUserEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Worker profile not found")
                );

        return new WorkerResponse(
                worker.getId(),
                worker.getUser().getName(),
                worker.getCategory(),
                worker.getExperience(),
                worker.getAvailable()
        );
    }

    private ComplaintResponse convertToResponse(
            Complaint complaint,
            boolean includeCustomerDetails
    ) {
        Long workerId = null;
        String workerName = null;

        if (complaint.getWorker() != null) {
            workerId = complaint.getWorker().getId();
            workerName = complaint.getWorker().getUser().getName();
        }

        return new ComplaintResponse(
                complaint.getId(),
                complaint.getTitle(),
                complaint.getDescription(),
                complaint.getCategory(),
                complaint.getPriority(),
                complaint.getStatus(),
                complaint.getCreatedAt(),
                complaint.getResolvedAt(),
                workerId,
                workerName,
                complaint.getAddress(),
                complaint.getLandmark(),
                complaint.getCity(),
                complaint.getPincode(),
                includeCustomerDetails
                        ? complaint.getUser().getName()
                        : null,
                includeCustomerDetails
                        ? complaint.getUser().getPhone()
                        : null
        );
    }


}
