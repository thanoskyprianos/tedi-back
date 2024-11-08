package com.network.network.media;

import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.media.service.MediaService;
import com.network.network.misc.View;
import com.network.network.user.User;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "users/{userId}/media", produces = "application/hal+json")
public class UserMediaController {
    @Resource private MediaService mediaService;

    @Resource private UserService userService;

    @GetMapping("/avatar")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> getUserAvatar(@PathVariable int userId) {
        User user = userService.getUserById(userId);
        Media media = user.getAvatar();

        if (media == null) {
            return ResponseEntity.noContent().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", media.getContentType());

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(mediaService.fetchMedia(media));
    }

    @PostMapping("/avatar") @PreAuthorize("#userId == principal.getId()")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> uploadUserAvatar(@PathVariable int userId, @RequestParam MultipartFile file) {
        User user = userService.getUserById(userId);

        if (user.getAvatar() != null) {
            return updateUserAvatar(userId, file);
        }

        Media media = mediaService.saveFile(file);
        user.setAvatar(media);

        userService.updateUser(user);

        return ResponseEntity.ok(media);
    }

    @PutMapping("/avatar") @PreAuthorize("#userId == principal.getId()")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> updateUserAvatar(@PathVariable int userId, @RequestParam MultipartFile file) {
        User user = userService.getUserById(userId);

        if (user.getAvatar() == null) {
            return uploadUserAvatar(userId, file);
        }

        Media media = user.getAvatar();
        media = mediaService.updateFile(media.getId(), file);

        return ResponseEntity.ok(media);
    }
}
