package ResolveX.controller;

import ResolveX.dto.ComplaintCreateRequest;
import ResolveX.dto.ComplaintResponse;
import ResolveX.service.ComplaintService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@AllArgsConstructor
public class ComplaintController {


    private final ComplaintService complaintService;


    @PostMapping
    public ResponseEntity<ComplaintResponse> createComplaint(
            @Valid @RequestBody ComplaintCreateRequest request,
            Authentication authentication
    ) {
        ComplaintResponse response = complaintService.createComplaint(
                request,
                authentication.getName()
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/my")
    public ResponseEntity<List<ComplaintResponse>> getMyComplaints(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                complaintService.getMyComplaints(authentication.getName())
        );
    }


    @GetMapping("/{complaintId}")
    public ResponseEntity<ComplaintResponse> getComplaintById(
            @PathVariable Long complaintId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                complaintService.getComplaintById(
                        complaintId,
                        authentication.getName()
                )
        );
    }


    @GetMapping("/{complaintId}/customer-details")
    public ResponseEntity<ComplaintResponse> getCustomerDetails(
            @PathVariable Long complaintId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                complaintService.getComplaintForWorker(
                        complaintId,
                        authentication.getName()
                )
        );
    }


    @PutMapping("/{complaintId}/assign/{workerId}")
    public ResponseEntity<ComplaintResponse> assignWorker(
            @PathVariable Long complaintId,
            @PathVariable Long workerId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                complaintService.assignWorker(
                        complaintId,
                        workerId,
                        authentication.getName()
                )
        );
    }


    @PutMapping("/{complaintId}/accept")
    public ResponseEntity<ComplaintResponse> acceptComplaint(
            @PathVariable Long complaintId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                complaintService.acceptComplaint(
                        complaintId,
                        authentication.getName()
                )
        );
    }


    @PutMapping("/{complaintId}/cancel")
    public ResponseEntity<ComplaintResponse> cancelAssignment(
            @PathVariable Long complaintId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                complaintService.cancelAssignment(
                        complaintId,
                        authentication.getName()
                )
        );
    }


    @PutMapping("/{complaintId}/resolve")
    public ResponseEntity<ComplaintResponse> resolveComplaint(
            @PathVariable Long complaintId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                complaintService.resolveComplaint(
                        complaintId,
                        authentication.getName()
                )
        );
    }


}
