package ResolveX.service;

import ResolveX.dto.NotificationResponse;
import ResolveX.entity.Complaint;
import ResolveX.entity.Notification;
import ResolveX.entity.User;
import ResolveX.repository.NotificationRepository;
import ResolveX.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public Notification createNotification(
            User recipient,
            Complaint complaint,
            String message
    ) {
        Notification notification = new Notification();

        notification.setRecipient(recipient);
        notification.setComplaint(complaint);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    public List<NotificationResponse> getMyNotifications(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return notificationRepository
                .findByRecipientOrderByCreatedAtDesc(user)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public long getUnreadCount(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return notificationRepository.countByRecipientAndReadFalse(user);
    }

    public NotificationResponse markAsRead(Long notificationId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new RuntimeException("Notification not found")
                );

        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to access this notification"
            );
        }

        notification.setRead(true);

        Notification savedNotification =
                notificationRepository.save(notification);

        return convertToResponse(savedNotification);
    }

    private NotificationResponse convertToResponse(
            Notification notification
    ) {
        return new NotificationResponse(
                notification.getId(),
                notification.getComplaint().getId(),
                notification.getComplaint().getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}