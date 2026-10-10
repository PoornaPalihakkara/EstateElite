package com.estateelite.service;

import com.estateelite.dto.response.NotificationResponse;
import com.estateelite.model.NotificationType;
import com.estateelite.model.User;
import com.estateelite.pattern.observer.NotificationEvent;
import com.estateelite.pattern.observer.NotificationPublisher;
import com.estateelite.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationPublisher publisher;
    private final NotificationRepository notificationRepository;

    public void notify(User recipient, String message, NotificationType type) {
        publisher.publish(new NotificationEvent(recipient, message, type));
    }

    public List<NotificationResponse> getInbox(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId)
                .stream().map(NotificationResponse::from).collect(Collectors.toList());
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.findByRecipientIdAndIsReadFalse(userId).size();
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

    public void markAllAsRead(Long userId) {
        notificationRepository.findByRecipientIdAndIsReadFalse(userId)
                .forEach(n -> { n.setIsRead(true); notificationRepository.save(n); });
    }
}
