package com.network.network.post;

import com.network.network.comment.Comment;
import com.network.network.comment.resource.CommentResourceAssembler;
import com.network.network.comment.service.CommentService;
import com.network.network.misc.HelperService;
import com.network.network.post.resource.PostResourceAssembler;
import com.network.network.post.service.PostService;
import com.network.network.user.User;
import com.network.network.user.resource.UserResourceAssembler;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/posts")
@CrossOrigin("*")
public class PostController {
    @Resource
    private PostService postService;

    @Resource
    private PostResourceAssembler postResourceAssembler;

    @Resource
    private UserService userService;

    @Resource
    private UserResourceAssembler userResourceAssembler;

    @Resource
    private CommentService commentService;

    @Resource
    private CommentResourceAssembler commentResourceAssembler;

    @Resource
    private HelperService helperService;

    @GetMapping("")
    public ResponseEntity<?> getUserPosts(@PathVariable int userId) {
        User user = userService.getUserById(userId);

        return ResponseEntity.ok(postResourceAssembler.toCollectionModel(user.getPosts()));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getUserPost(@PathVariable int userId, @PathVariable int postId) {
        Post post = helperService.getPostByPair(userId, postId);

        return ResponseEntity.ok(postResourceAssembler.toModel(post));
    }


    @PostMapping("") @Transactional
    @PreAuthorize("#userId == principal.getId()")
    public ResponseEntity<?> addPost(@PathVariable int userId, @RequestBody Post post) {
        User user = userService.getUserById(userId);

        post.setUser(user);
        post = postService.savePost(post);

        user.addPost(post);
        userService.updateUser(user);

        EntityModel<Post> postModel = postResourceAssembler.toModel(post);

        return ResponseEntity
                .created(postModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(postModel);
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("#userId == principal.getId() || hasRole('ADMIN')")
    public ResponseEntity<?> deleteUserPost(@PathVariable int userId, @PathVariable int postId) {
        Post post = helperService.getPostByPair(userId, postId);

        postService.removePost(post);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/liked")
    public ResponseEntity<?> getUserLikedPosts(@PathVariable int userId) {
        User user = userService.getUserById(userId);

        return ResponseEntity.ok(postResourceAssembler.toCollectionModel(user.getLiked()));
    }

    @GetMapping("/{postId}/likes")
    public ResponseEntity<?> likesForPost(
            @PathVariable int userId,
            @PathVariable int postId
    ) {
        Post post = helperService.getPostByPair(userId, postId);

        return ResponseEntity.ok(userResourceAssembler.toUserCollectionModel(post.getLikedBy()));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> commentsForPost(
            @PathVariable int userId,
            @PathVariable int postId
    ) {
        Post post = helperService.getPostByPair(userId, postId);

        return ResponseEntity.ok(commentResourceAssembler.toCollectionModel(post.getComments()));
    }

    @Transactional
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(
            @PathVariable int userId,
            @PathVariable int postId
    ) {
        User user = userService.getPrincipal();
        Post post = helperService.getPostByPair(userId, postId);

        user.addLiked(post);
        post.addLikedBy(user);

        postService.savePost(post);
        userService.updateUser(user);

        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PostMapping("/{postId}/comment")
    public ResponseEntity<?> commentPost(
            @PathVariable int userId,
            @PathVariable int postId,
            @RequestBody Comment comment
    ) {
        User user = userService.getPrincipal();
        Post post = helperService.getPostByPair(userId, postId);

        user.addComment(comment);
        post.addComment(comment);

        comment.setUser(user);
        comment.setPost(post);

        postService.savePost(post);
        userService.updateUser(user);
        commentService.saveComment(comment);

        return ResponseEntity.noContent().build();
    }
}
