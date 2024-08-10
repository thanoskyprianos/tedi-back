package com.network.network.comment;

import com.network.network.comment.resource.CommentResourceAssembler;
import com.network.network.comment.service.CommentService;
import com.network.network.misc.HelperService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/posts/{postId}/comments")
public class CommentController {
    @Resource
    private HelperService helperService;

    @Resource
    private CommentService commentService;

    @Resource
    private CommentResourceAssembler commentResourceAssembler;

    @GetMapping("/{commentId}")
    public ResponseEntity<?> getComment(
            @PathVariable int userId,
            @PathVariable int postId,
            @PathVariable int commentId
    ) {
        Comment comment = helperService.getCommentByTriplet(userId, postId, commentId);

        return ResponseEntity.ok(commentResourceAssembler.toModel(comment));
    }

    @PreAuthorize("#userId == principal.getId() || hasRole('ADMIN')")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable int userId,
            @PathVariable int postId,
            @PathVariable int commentId
    ) {
        Comment comment = helperService.getCommentByTriplet(userId, postId, commentId);

        commentService.deleteComment(comment);

        return ResponseEntity.noContent().build();
    }
}
