package com.network.network.connections.service;

import com.network.network.connections.exception.ConnectionPendingException;
import com.network.network.notification.exception.NotificationNotFoundException;
import com.network.network.connections.ConnectionNotification;
import com.network.network.connections.resource.ConnectionNotificationRepository;
import com.network.network.user.User;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ConnectionService {
    @Resource
    private ConnectionNotificationRepository connectionNotificationRepository;

    @Resource
    private UserService userService;

    public List<ConnectionNotification> getSent(int userId) {
        return userService.getUserById(userId).getRequests();
    }

    public ConnectionNotification getSent(int userId, int notificationId) {
        return connectionNotificationRepository
                .getConnectionNotificationByIdAndSenderId(notificationId, userId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));
    }

    public List<ConnectionNotification> getReceived(int userId) {
        return userService.getUserById(userId).getReceived();
    }

    public ConnectionNotification getReceived(int userId, int notificationId) {
        return connectionNotificationRepository
                .getConnectionNotificationByIdAndReceiverId(notificationId, userId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));
    }

    public ConnectionNotification getRequest(int userId1, int userId2) {
        Optional<ConnectionNotification> not1 =
                connectionNotificationRepository
                        .getConnectionNotificationByReceiverIdAndSenderId(userId1, userId2);

        Optional<ConnectionNotification> not2 = connectionNotificationRepository
                .getConnectionNotificationByReceiverIdAndSenderId(userId2, userId1);

        return not1.orElse(not2.orElse(null));

    }

    @Transactional
    public ConnectionNotification createNotification(User sender, User receiver) {
        if (getRequest(sender.getId(), receiver.getId()) != null) {
            throw new ConnectionPendingException();
        }

        ConnectionNotification connectionNotification = new ConnectionNotification(sender, receiver);
        sender.addRequest(connectionNotification);
        receiver.addReceived(connectionNotification);

        userService.updateUser(sender);
        userService.updateUser(receiver);

        return connectionNotificationRepository.save(connectionNotification);
    }

    @Transactional
    public void accept(ConnectionNotification notification) {
        User sender = notification.getSender();
        User receiver = notification.getReceiver();

        sender.addConnected(receiver);
        receiver.addConnected(sender);

        userService.updateUser(sender);
        userService.updateUser(receiver);

        connectionNotificationRepository.delete(notification);
    }

    public void reject(ConnectionNotification notification) {
        connectionNotificationRepository.delete(notification);
    }

    public void cancel(ConnectionNotification notification) {
        connectionNotificationRepository.delete(notification);
    }

    @Transactional
    public void remove(User sender, User receiver) {
        sender.getConnected().remove(receiver);
        receiver.getConnected().remove(sender);

        userService.updateUser(sender);
        userService.updateUser(receiver);
    }
}
