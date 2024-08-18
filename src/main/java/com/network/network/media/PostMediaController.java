package com.network.network.media;

import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.media.exception.MediaNotExistsException;
import com.network.network.media.service.MediaService;
import com.network.network.misc.View;
import com.network.network.post.Post;
import com.network.network.post.service.PostService;
import com.network.network.user.User;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/users/{userId}/posts/{postId}/media", produces = "application/hal+json")
public class PostMediaController {
    @Resource
    private UserService userService;

    @Resource
    private PostService postService;

    @Resource
    private MediaService mediaService;

    @GetMapping("/{mediaIdx}")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> getMedia(@PathVariable int userId, @PathVariable int postId, @PathVariable int mediaIdx) {
        User user = userService.getUserById(userId);
        Post post = postService.getPostByIdAndUser(postId, user);

        Media media;
        try {
            media = post.getMediaList().get(mediaIdx);
        } catch (IndexOutOfBoundsException e) {
            throw new MediaNotExistsException(mediaIdx);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", media.getContentType());

        return ResponseEntity.ok().headers(headers).body(mediaService.fetchMedia(media));
    }

    @PostMapping("")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#userId == principal.getId()")
    public ResponseEntity<?> addMedia(
            @PathVariable int userId,
            @PathVariable int postId,
            @RequestParam MultipartFile file)
    {
        User user = userService.getUserById(userId);
        Post post = postService.getPostByIdAndUser(postId, user);

        Media media = mediaService.saveFile(file);
        post.addMedia(media);
        postService.savePost(post);

        return ResponseEntity.ok(media);
    }
}
