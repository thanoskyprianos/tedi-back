package com.network.network.media;

import com.network.network.media.resource.MediaResourceAssembler;
import com.network.network.media.service.MediaService;
import com.network.network.user.User;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin("*")
@RequestMapping("user/{userId}/media/avatar")
@PreAuthorize("#userId == principal.getId()")
public class UserMediaController {
    @Resource private MediaService mediaService;

    @Resource private UserService userService;

    @GetMapping("")
    public ResponseEntity<?> getUserAvatar(@PathVariable int userId) {
        User user = userService.getUserById(userId);
        Media media = user.getAvatar();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", media.getContentType());

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(mediaService.fetchMedia(media));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadUserAvatar(@PathVariable int userId, @RequestParam MultipartFile file) {
        Media media = mediaService.saveFile(file);

        User user = userService.getUserById(userId);
        user.setAvatar(media);

        userService.saveUser(user);

        return ResponseEntity.ok(media);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUserAvatar(@PathVariable int userId, @RequestParam MultipartFile file) {
        Media media = userService.getUserById(userId).getAvatar();
        media = mediaService.updateFile(media.getId(), file);

        return ResponseEntity.ok(media);
    }
}
