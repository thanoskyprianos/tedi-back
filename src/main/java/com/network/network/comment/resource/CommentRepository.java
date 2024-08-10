package com.network.network.comment.resource;

import com.network.network.comment.Comment;
import com.network.network.post.Post;
import com.network.network.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Optional<Comment> findByIdAndUserAndPost(Integer id, User user, Post post);
}
