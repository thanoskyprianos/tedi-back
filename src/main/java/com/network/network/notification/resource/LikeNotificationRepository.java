package com.network.network.notification.resource;

import com.network.network.notification.modules.LikeNotification;
import com.network.network.post.Post;
import com.network.network.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeNotificationRepository extends JpaRepository<LikeNotification, Integer> {
    Optional<LikeNotification> findByPostAndSenderAndReceiver(Post post, User sender, User receiver);
}
