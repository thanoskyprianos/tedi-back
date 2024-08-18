package com.network.network.comment;

import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.comment.resource.CommentResourceAssembler;
import com.network.network.comment.service.CommentService;
import com.network.network.misc.HelperService;
import com.network.network.misc.View;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users/{userId}/posts/{postId}/comments", produces = "application/hal+json")
public class CommentController {
    @Resource
    private HelperService helperService;

    @Resource
    private CommentService commentService;

    @Resource
    private CommentResourceAssembler commentResourceAssembler;

    @GetMapping("/{commentId}")
    @JsonView(View.AsProfessional.class)
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
    @JsonView(View.AsProfessional.class)
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
