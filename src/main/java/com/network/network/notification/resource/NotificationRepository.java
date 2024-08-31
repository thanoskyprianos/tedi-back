package com.network.network.notification.resource;

import com.network.network.notification.modules.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
}
