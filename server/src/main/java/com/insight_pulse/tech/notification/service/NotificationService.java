package com.insight_pulse.tech.notification.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.insight_pulse.tech.notification.domain.Notification;
import com.insight_pulse.tech.notification.domain.NotificationRepository;
import com.insight_pulse.tech.notification.dto.NotificationSummary;
import com.insight_pulse.tech.notification.dto.NotificationRequest;
import com.insight_pulse.tech.notification.dto.NotificationResponse;
import com.insight_pulse.tech.security.context.CurrentUserProvider;
import com.insight_pulse.tech.user.domain.User;
import com.insight_pulse.tech.user.domain.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;
    public void createNotification(String title, String message, int userId, String type, String link) {
        if (title == null) throw new IllegalArgumentException("Title cannot be null!");
        if (message == null) throw new IllegalArgumentException("Message cannot be null!");
        if (userId <= 0) throw new IllegalArgumentException("UserId không hợp lệ");
        String recipientEmail = userRepository.findById(userId)
                .map(User::getEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Notification notification = Notification.create(
            title,
            message,
            userId,
            type,
            link
        );
        notificationRepository.save(notification);
        NotificationRequest response = new NotificationRequest(title, message, userId, type, link);
        messagingTemplate.convertAndSendToUser(
                    recipientEmail,
                    "/queue/notifications",
                    response
        );
    } 

    public NotificationSummary getNotifications(Pageable pageable) {
        int userId = currentUserProvider.getCurrentUserId(); 
        Page<Notification> notifications = notificationRepository.findAllByUserId(pageable, userId);        
        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(userId);
        Page<NotificationResponse> notificationResponse = notifications.map(n -> new NotificationResponse(
            n.getId(),
            n.getTitle(),
            n.getMessage(),
            n.getType(),
            n.getLink(),
            n.isRead(),
            n.getCreatedAt()
        ));
        return new NotificationSummary(
            notificationResponse,
            unreadCount
        ); 
    }

    public void markAsRead(long id) {
        int userId = currentUserProvider.getCurrentUserId();
        Notification notification = notificationRepository.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
