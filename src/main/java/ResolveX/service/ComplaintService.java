package ResolveX.service;

import ResolveX.dto.ComplaintCreateRequest;
import ResolveX.dto.ComplaintResponse;
import ResolveX.entity.Complaint;
import ResolveX.entity.ComplaintStatus;
import ResolveX.entity.User;
import ResolveX.entity.Worker;
import ResolveX.repository.ComplaintRepository;
import ResolveX.repository.UserRepository;
import ResolveX.repository.WorkerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ResolveX.entity.WorkerComplaintRejection;
import ResolveX.repository.WorkerComplaintRejectionRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ComplaintService {


    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final NotificationService notificationService;
    private final WorkerComplaintRejectionRepository rejectionRepository;

    public ComplaintResponse createComplaint(
            ComplaintCreateRequest request,
            String email
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        Complaint complaint = new Complaint();

        complaint.setUser(user);
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setCategory(request.getCategory());
        complaint.setPriority(request.getPriority());

        complaint.setAddress(request.getAddress());
        complaint.setLandmark(request.getLandmark());
        complaint.setCity(request.getCity());
        complaint.setPincode(request.getPincode());

        complaint.setStatus(ComplaintStatus.PENDING);
        complaint.setCreatedAt(LocalDateTime.now());

        Complaint savedComplaint = complaintRepository.save(complaint);

        return convertToResponse(savedComplaint);
    }

    public List<ComplaintResponse> getMyComplaints(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return complaintRepository.findByUserId(user.getId())
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public ComplaintResponse assignWorker(
            Long complaintId,
            Long workerId,
            String email
    ) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() ->
                        new RuntimeException("Complaint not found")
                );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        if (!complaint.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to assign this complaint"
            );
        }

        if (complaint.getStatus() != ComplaintStatus.PENDING) {
            throw new RuntimeException(
                    "Only PENDING complaints can be assigned"
            );
        }

        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() ->
                        new RuntimeException("Worker not found")
                );

        if (!Boolean.TRUE.equals(worker.getAvailable())) {
            throw new RuntimeException("Worker is not available");
        }

        complaint.setWorker(worker);

        Complaint savedComplaint = complaintRepository.save(complaint);

        notificationService.createNotification(
                worker.getUser(),
                savedComplaint,
                "New complaint assigned to you: "
                        + savedComplaint.getTitle()
        );

        return convertToResponse(savedComplaint);
    }

    @Transactional
    public ComplaintResponse acceptComplaint(
            Long complaintId,
            String email
    ) {
        Complaint complaint = complaintRepository.findByIdForUpdate(complaintId)
                .orElseThrow(() ->
                        new RuntimeException("Complaint not found")
                );

        Worker worker = workerRepository.findByUserEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Worker profile not found")
                );

        if (!Boolean.TRUE.equals(worker.getAvailable())) {
            throw new RuntimeException("Worker is not available");
        }

        if (complaint.getStatus() != ComplaintStatus.PENDING) {
            throw new RuntimeException(
                    "Complaint is not in PENDING status"
            );
        }

        if (complaint.getWorker() != null) {
            throw new RuntimeException(
                    "Complaint has already been assigned"
            );
        }

        if (!complaint.getCategory().equalsIgnoreCase(worker.getCategory())) {
            throw new RuntimeException(
                    "Complaint category does not match your category"
            );
        }

        complaint.setWorker(worker);
        complaint.setStatus(ComplaintStatus.ACCEPTED);

        worker.setAvailable(false);
        workerRepository.save(worker);

        Complaint savedComplaint = complaintRepository.save(complaint);

        notificationService.createNotification(
                savedComplaint.getUser(),
                savedComplaint,
                worker.getUser().getName()
                        + " has accepted your complaint: "
                        + savedComplaint.getTitle()
        );

        return convertToResponse(savedComplaint);
    }


    @Transactional
    public ComplaintResponse cancelAssignment(
            Long complaintId,
            String email
    ) {
        Complaint complaint = complaintRepository.findByIdForUpdate(complaintId)
                .orElseThrow(() ->
                        new RuntimeException("Complaint not found")
                );

        Worker worker = workerRepository.findByUserEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Worker profile not found")
                );

        if (complaint.getStatus() != ComplaintStatus.PENDING) {
            throw new RuntimeException(
                    "Only PENDING complaints can be rejected"
            );
        }

        if (complaint.getWorker() != null) {
            throw new RuntimeException(
                    "This complaint is already assigned"
            );
        }

        boolean alreadyRejected =
                rejectionRepository.existsByComplaint_IdAndWorker_Id(
                        complaintId,
                        worker.getId()
                );

        if (!alreadyRejected) {
            WorkerComplaintRejection rejection =
                    new WorkerComplaintRejection(
                            complaint,
                            worker,
                            LocalDateTime.now()
                    );

            rejectionRepository.save(rejection);
        }

        return convertToResponse(complaint);
    }
    public ComplaintResponse resolveComplaint(
            Long complaintId,
            String email
    ) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() ->
                        new RuntimeException("Complaint not found")
                );

        Worker worker = workerRepository.findByUserEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Worker profile not found")
                );

        if (complaint.getWorker() == null
                || !complaint.getWorker().getId().equals(worker.getId())) {
            throw new RuntimeException(
                    "This complaint is not assigned to you"
            );
        }

        if (complaint.getStatus() != ComplaintStatus.ACCEPTED) {
            throw new RuntimeException(
                    "Only ACCEPTED complaints can be resolved"
            );
        }

        complaint.setStatus(ComplaintStatus.RESOLVED);
        complaint.setResolvedAt(LocalDateTime.now());

        Complaint savedComplaint = complaintRepository.save(complaint);

        worker.setAvailable(true);
        workerRepository.save(worker);

        return convertToResponse(savedComplaint);
    }

    public ComplaintResponse getComplaintById(
            Long complaintId,
            String email
    ) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() ->
                        new RuntimeException("Complaint not found")
                );

        if (!complaint.getUser().getEmail().equals(email)) {
            throw new RuntimeException(
                    "You are not allowed to view this complaint"
            );
        }

        return convertToResponse(complaint);
    }

    public ComplaintResponse getComplaintForWorker(
            Long complaintId,
            String email
    ) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() ->
                        new RuntimeException("Complaint not found")
                );

        Worker worker = workerRepository.findByUserEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Worker profile not found")
                );

        if (complaint.getWorker() == null
                || !complaint.getWorker().getId().equals(worker.getId())
                || (complaint.getStatus() != ComplaintStatus.ACCEPTED
                && complaint.getStatus() != ComplaintStatus.RESOLVED)) {
            throw new RuntimeException(
                    "You are not allowed to view this customer's details"
            );
        }

        return convertToResponse(complaint);
    }

    private ComplaintResponse convertToResponse(Complaint complaint) {
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
                complaint.getUser().getName(),
                complaint.getUser().getPhone()
        );
    }


}
