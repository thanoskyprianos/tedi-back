package com.network.network.post;

import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.comment.Comment;
import com.network.network.comment.resource.CommentResourceAssembler;
import com.network.network.comment.service.CommentService;
import com.network.network.misc.HelperService;
import com.network.network.misc.View;
import com.network.network.notification.modules.CommentNotification;
import com.network.network.notification.modules.InterestNotification;
import com.network.network.notification.modules.LikeNotification;
import com.network.network.notification.resource.CommentNotificationRepository;
import com.network.network.notification.resource.InterestNotificationRepository;
import com.network.network.notification.resource.LikeNotificationRepository;
import com.network.network.post.resource.PostResourceAssembler;
import com.network.network.post.service.PostService;
import com.network.network.user.User;
import com.network.network.user.resource.UserResourceAssembler;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping(value = "/users/{userId}/posts", produces = "application/hal+json")
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

    @Resource
    private LikeNotificationRepository likeNotificationRepository;

    @Resource
    private InterestNotificationRepository interestNotificationRepository;

    @Resource
    private CommentNotificationRepository commentNotificationRepository;

    // todo: use matrix factorization
    @GetMapping("/for")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#userId == principal.getId()")
    public ResponseEntity<?> getPostsForUser(@PathVariable int userId) {
        return ResponseEntity.ok(postResourceAssembler.toCollectionModel(postService.getAllPosts()));
    }

    @GetMapping("")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> getUserPosts(@PathVariable int userId) {
        if(helperService.notAccessible(userId)) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }

        User user = userService.getUserById(userId);
        return ResponseEntity.ok(postResourceAssembler.toCollectionModel(user.getPosts()));
    }

    @GetMapping("/job-offers")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> getJobOfferPosts(@PathVariable int userId) {
        return ResponseEntity.ok(postResourceAssembler.toCollectionModel(postService.getJobOfferPosts()));
    }

    @GetMapping("/{postId}")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> getUserPost(@PathVariable int userId, @PathVariable int postId) {
        if (helperService.notAccessible(userId)) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }

        Post post = helperService.getPostByPair(userId, postId);
        return ResponseEntity.ok(postResourceAssembler.toModel(post));
    }


    @PostMapping("") @Transactional
    @JsonView(View.AsProfessional.class)
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
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#userId == principal.getId() || hasRole('ADMIN')")
    public ResponseEntity<?> deleteUserPost(@PathVariable int userId, @PathVariable int postId) {
        Post post = helperService.getPostByPair(userId, postId);

        postService.removePost(post);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/liked")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> getUserLikedPosts(@PathVariable int userId) {
        if(helperService.notAccessible(userId)) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }

        User user = userService.getUserById(userId);

        return ResponseEntity.ok(postResourceAssembler.toCollectionModel(user.getLiked()));
    }

    @GetMapping("/{postId}/likes")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> likesForPost(
            @PathVariable int userId,
            @PathVariable int postId
    ) {
        if (helperService.notAccessible(userId)) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }

        Post post = helperService.getPostByPair(userId, postId);

        return ResponseEntity.ok(userResourceAssembler.toCollectionModel(post.getLikedBy()));
    }

    @GetMapping("/{postId}/comments")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> commentsForPost(
            @PathVariable int userId,
            @PathVariable int postId
    ) {
        if (helperService.notAccessible(userId)) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }

        Post post = helperService.getPostByPair(userId, postId);

        List<Comment> comments = post.getComments();
        comments.sort(Comparator.comparing(Comment::getCreated).reversed());

        return ResponseEntity.ok(commentResourceAssembler.toCollectionModel(comments));
    }

    @Transactional
    @PostMapping("/{postId}/like")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> likePost(
            @PathVariable int userId,
            @PathVariable int postId
    ) {
        if (helperService.notAccessible(userId)) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }

        User user = userService.getPrincipal();
        Post post = helperService.getPostByPair(userId, postId);

        if (user.getLiked().contains(post)) {
            user.getLiked().remove(post);
            post.getLikedBy().remove(user);

            if (!post.isJobOffer()) {
                likeNotificationRepository
                        .findByPostAndSenderAndReceiver(post, user, post.getUser())
                        .ifPresent(likeNotificationRepository::delete);
            } else {
                interestNotificationRepository
                        .findByPostAndSenderAndReceiver(post, user, post.getUser())
                        .ifPresent(interestNotificationRepository::delete);
            }

        } else {
            user.addLiked(post);
            post.addLikedBy(user);

            if (!post.isJobOffer()) {
                LikeNotification likeNotification
                        = new LikeNotification(post, user, post.getUser());

                likeNotificationRepository.save(likeNotification);

                user.addSentLikeNotification(likeNotification);
                post.getUser().addReceivedLikeNotification(likeNotification);
                post.addLikedNotification(likeNotification);
            } else {
                InterestNotification interestNotification
                        = new InterestNotification(post, user, post.getUser());

                interestNotificationRepository.save(interestNotification);

                user.addSentInterestNotification(interestNotification);
                post.getUser().addReceivedInterestNotification(interestNotification);
                post.addInterestNotification(interestNotification);
            }
        }

        userService.updateUser(user);
        userService.updateUser(post.getUser());
        postService.savePost(post);

        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PostMapping("/{postId}/comment")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> commentPost(
            @PathVariable int userId,
            @PathVariable int postId,
            @RequestBody Comment comment
    ) {
        if (helperService.notAccessible(userId)) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }

        User user = userService.getPrincipal();
        Post post = helperService.getPostByPair(userId, postId);

        user.addComment(comment);
        post.addComment(comment);

        comment.setUser(user);
        comment.setPost(post);

        CommentNotification commentNotification =
                new CommentNotification(comment, user, post.getUser());

        user.addSentCommentNotification(commentNotification);
        post.getUser().addReceivedCommentNotification(commentNotification);
        comment.addCommentNotification(commentNotification);

        commentNotificationRepository.save(commentNotification);

        postService.savePost(post);
        userService.updateUser(user);
        commentService.saveComment(comment);

        return ResponseEntity.noContent().build();
    }
}
