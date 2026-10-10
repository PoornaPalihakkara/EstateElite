package com.estateelite.pattern.observer;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationObserver implements NotificationObserver {

    private final NotificationPublisher publisher;

    @PostConstruct
    public void register() {
        publisher.subscribe(this);
    }

    @Override
    public void onNotify(NotificationEvent event) {
        log.info("[EMAIL SIMULATION] To: {} | Subject: Estate Elite Alert | Body: {}",
                event.getRecipient().getEmail(), event.getMessage());
    }
}
