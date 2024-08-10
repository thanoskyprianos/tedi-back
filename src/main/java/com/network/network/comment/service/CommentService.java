package com.network.network.comment.service;

import com.network.network.comment.Comment;
import com.network.network.comment.exception.CommentNotFoundException;
import com.network.network.comment.resource.CommentRepository;
import com.network.network.post.Post;
import com.network.network.user.User;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    @Resource
    private CommentRepository commentRepository;

    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }

    public Comment getCommentByIdAndUserAndPost(int commentId, User user, Post post) {
        return commentRepository
                .findByIdAndUserAndPost(commentId, user, post)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }
}
