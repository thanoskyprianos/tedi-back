package com.network.network.notification;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("LIKE")
public class LikeNotification extends Notification {
}
