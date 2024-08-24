package com.network.network.connections;

import com.network.network.notification.Notification;
import com.network.network.user.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("CONNECTION")
public class ConnectionNotification extends Notification {
    @ManyToOne
    private User sender;

    @ManyToOne
    private User receiver;
}
