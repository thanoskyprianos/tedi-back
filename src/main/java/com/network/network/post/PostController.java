package com.network.network.post;

import com.network.network.post.resource.PostResourceAssembler;
import com.network.network.post.service.PostService;
import com.network.network.user.User;
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
public class PostController {
    @Resource
    private PostService postService;

    @Resource
    private PostResourceAssembler postResourceAssembler;

    @Resource
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<?> posts(@PathVariable int userId) {
        User user = userService.getUserById(userId);

        return ResponseEntity.ok(postResourceAssembler.toCollectionModel(user.getPosts()));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> post(@PathVariable int userId, @PathVariable int postId) {
        User user = userService.getUserById(userId);
        Post post = postService.getPostByIdAndUser(postId, user);

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
    public ResponseEntity<?> deletePost(@PathVariable int userId, @PathVariable int postId) {
        User user = userService.getUserById(userId);

        Post post = postService.getPostByIdAndUser(postId, user);
        postService.removePost(post);

        return ResponseEntity.ok().build();
    }
}
