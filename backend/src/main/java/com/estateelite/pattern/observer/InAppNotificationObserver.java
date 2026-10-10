package com.estateelite.pattern.observer;

import com.estateelite.model.Notification;
import com.estateelite.repository.NotificationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InAppNotificationObserver implements NotificationObserver {

    private final NotificationRepository notificationRepository;
    private final NotificationPublisher publisher;

    @PostConstruct
    public void register() {
        publisher.subscribe(this);
    }

    @Override
    public void onNotify(NotificationEvent event) {
        Notification notification = Notification.builder()
                .recipient(event.getRecipient())
                .message(event.getMessage())
                .type(event.getType())
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }
}
