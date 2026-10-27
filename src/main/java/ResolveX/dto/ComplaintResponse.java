package ResolveX.dto;

import ResolveX.entity.ComplaintStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintResponse {


    private Long id;

    private String title;

    private String description;

    private String category;

    private String priority;

    private ComplaintStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    private Long workerId;

    private String workerName;

    private String address;

    private String landmark;

    private String city;

    private String pincode;

    private String customerName;

    private String customerPhone;


}
