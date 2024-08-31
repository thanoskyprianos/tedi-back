package com.network.network.notification.resource;

import com.network.network.notification.modules.InterestNotification;
import com.network.network.post.Post;
import com.network.network.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterestNotificationRepository extends JpaRepository<InterestNotification, Integer> {
    Optional<InterestNotification> findByPostAndSenderAndReceiver(Post post, User sender, User receiver);
}
