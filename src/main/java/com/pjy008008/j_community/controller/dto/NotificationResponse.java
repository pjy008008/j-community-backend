package com.pjy008008.j_community.controller.dto;

import com.pjy008008.j_community.entity.Notification;
import java.time.Duration;
import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String type,
        String user,
        String userInitial,
        String action,
        String content,
        String time,
        boolean read
) {
    public static NotificationResponse from(Notification notification) {
        String username = notification.getActor().getUsername();

        return new NotificationResponse(
                notification.getId(),
                notification.getType().name().toLowerCase().split("_")[0],
                username,
                username.substring(0, 1),
                notification.getType().getActionText(),
                notification.getContent(),
                formatTimeAgo(notification.getCreatedAt()),
                notification.isRead()
        );
    }

    private static String formatTimeAgo(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createdAt, now);
        long seconds = duration.getSeconds();

        if (seconds < 60) return "방금 전";
        if (seconds < 3600) return (seconds / 60) + "분 전";
        if (seconds < 86400) return (seconds / 3600) + "시간 전";
        return (seconds / 86400) + "일 전";
    }
}