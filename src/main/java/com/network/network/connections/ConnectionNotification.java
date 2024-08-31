package com.network.network.connections;

import com.network.network.notification.modules.Notification;
import com.network.network.user.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("CONNECTION")
public class ConnectionNotification extends Notification {
    public ConnectionNotification(User sender, User receiver) {
        super();
        this.setSender(sender);
        this.setReceiver(receiver);
    }
}
