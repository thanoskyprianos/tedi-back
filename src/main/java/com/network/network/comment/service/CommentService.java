package com.network.network.comment.service;

import com.network.network.comment.Comment;
import com.network.network.comment.exception.CommentNotFoundException;
import com.network.network.comment.resource.CommentRepository;
import com.network.network.post.Post;
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

    public Comment getCommentByPost(int commentId, Post post) {
        return commentRepository
                .findByIdAndPost(commentId, post)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }
}
