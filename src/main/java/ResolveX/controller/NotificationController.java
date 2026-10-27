package ResolveX.controller;

import ResolveX.dto.NotificationResponse;
import ResolveX.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                notificationService.getMyNotifications(authentication.getName())
        );
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            Authentication authentication
    ) {
        long count = notificationService.getUnreadCount(
                authentication.getName()
        );

        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                notificationService.markAsRead(
                        notificationId,
                        authentication.getName()
                )
        );
    }
}

