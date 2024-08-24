package com.network.network.connections.resource;

import com.network.network.connections.ConnectionNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConnectionNotificationRepository extends JpaRepository<ConnectionNotification, Integer> {
    Optional<ConnectionNotification> getConnectionNotificationByIdAndSenderId(Integer id, Integer senderId);
    Optional<ConnectionNotification> getConnectionNotificationByIdAndReceiverId(Integer id, Integer receiverId);
    Optional<ConnectionNotification> getConnectionNotificationByReceiverIdAndSenderId(Integer id, Integer requesterId);
}
