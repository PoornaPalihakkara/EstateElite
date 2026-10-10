package com.estateelite.controller;

import com.estateelite.dto.response.NotificationResponse;
import com.estateelite.model.User;
import com.estateelite.service.CurrentUserService;
import com.estateelite.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getInbox() {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(notificationService.getInbox(user.getId()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(user.getId())));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        User user = currentUserService.getCurrentUser();
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.noContent().build();
    }
}
