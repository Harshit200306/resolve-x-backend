package ResolveX.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private Long complaintId;
    private String complaintTitle;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
}
