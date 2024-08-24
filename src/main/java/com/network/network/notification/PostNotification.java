package com.network.network.notification;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("POST")
public class PostNotification extends Notification {
    String post;
}
