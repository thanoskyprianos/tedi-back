package com.network.network.notification.exception;

public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException(int id) {
        super("Notification with id " + id + " not found");
    }
}
