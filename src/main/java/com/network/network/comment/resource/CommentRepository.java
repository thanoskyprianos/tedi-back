package com.network.network.comment.resource;

import com.network.network.comment.Comment;
import com.network.network.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Optional<Comment> findByIdAndPost(Integer id, Post post);
}
