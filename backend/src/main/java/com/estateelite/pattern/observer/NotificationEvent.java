package com.estateelite.pattern.observer;

import com.estateelite.model.NotificationType;
import com.estateelite.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationEvent {
    private User recipient;
    private String message;
    private NotificationType type;
}
