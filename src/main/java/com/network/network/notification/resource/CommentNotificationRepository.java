package com.network.network.notification.resource;

import com.network.network.comment.Comment;
import com.network.network.notification.modules.CommentNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentNotificationRepository extends JpaRepository<CommentNotification, Integer> {
    Optional<CommentNotification> findByComment(Comment comment);
}
