package ResolveX.controller;

import ResolveX.dto.ComplaintResponse;
import ResolveX.dto.WorkerResponse;
import ResolveX.service.WorkerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workers")
@AllArgsConstructor
public class WorkerController {


    private final WorkerService workerService;

    @GetMapping("/available")
    public ResponseEntity<List<WorkerResponse>> getAvailableWorkers() {
        return ResponseEntity.ok(workerService.getAvailableWorkers());
    }

    @GetMapping("/me")
    public ResponseEntity<WorkerResponse> getMyProfile(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                workerService.getMyProfile(authentication.getName())
        );
    }

    @GetMapping("/my-complaints")
    public ResponseEntity<List<ComplaintResponse>> getMyAssignedComplaints(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                workerService.getMyAssignedComplaints(authentication.getName())
        );
    }

    @GetMapping("/available-complaints")
    public ResponseEntity<List<ComplaintResponse>> getAvailableComplaints(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                workerService.getAvailableComplaints(authentication.getName())
        );
    }

    @PutMapping("/availability")
    public ResponseEntity<WorkerResponse> updateAvailability(
            @RequestBody Map<String, Boolean> request,
            Authentication authentication
    ) {
        Boolean available = request.get("available");

        if (available == null) {
            throw new RuntimeException("Availability is required");
        }

        return ResponseEntity.ok(
                workerService.updateAvailability(
                        authentication.getName(),
                        available
                )
        );
    }


}
