package com.network.network.notification.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.misc.View;
import com.network.network.notification.modules.CommentNotification;
import com.network.network.notification.modules.InterestNotification;
import com.network.network.notification.modules.LikeNotification;
import com.network.network.notification.resource.*;
import com.network.network.user.User;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping(value = "/users/{userId}/activities", produces = "application/hal+json")
public class ActivityController {
    @Resource
    private UserService userService;

    @Resource
    private NotificationResourceAssembler notificationResourceAssembler;

    @Resource
    private LikeNotificationRepository likeNotificationRepository;

    @Resource
    private CommentNotificationRepository commentNotificationRepository;

    @Resource
    private InterestNotificationRepository interestNotificationRepository;

    @GetMapping("/likes")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#userId == principal.getId()")
    public ResponseEntity<?> getLikeActivities(@PathVariable int userId) {
        User user = userService.getUserById(userId);

        List<LikeNotification> notifications = user.getReceivedLikeNotifications();
        notifications.sort(
                Comparator.comparing(LikeNotification::isRead).reversed()
                          .thenComparing(LikeNotification::getCreated).reversed());

        return ResponseEntity.ok(notificationResourceAssembler.toCollectionModel(notifications));
    }

    @GetMapping("/likes/{activityId}")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#userId == principal.getId()")
    public ResponseEntity<?> getLikeActivities(@PathVariable int userId, @PathVariable int activityId) {
        User user = userService.getUserById(userId);
        LikeNotification likeNotification = likeNotificationRepository.findById(activityId).orElse(null);

        if (likeNotification != null && user.getReceivedLikeNotifications().contains(likeNotification)) {
            return ResponseEntity.ok(notificationResourceAssembler.toModel(likeNotification));
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/likes/{activityId}/read")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#userId == principal.getId()")
    public ResponseEntity<?> readLikeActivities(@PathVariable int userId, @PathVariable int activityId) {
        User user = userService.getUserById(userId);
        LikeNotification likeNotification = likeNotificationRepository.findById(activityId).orElse(null);

        if (likeNotification != null && user.getReceivedLikeNotifications().contains(likeNotification)) {
            likeNotification.setRead(!likeNotification.isRead());
            likeNotificationRepository.save(likeNotification);

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/likes/{activityId}/delete")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#userId == principal.getId()")
    public ResponseEntity<?> deleteLikeActivities(@PathVariable int userId, @PathVariable int activityId) {
        User user = userService.getUserById(userId);
        LikeNotification likeNotification = likeNotificationRepository.findById(activityId).orElse(null);

        if (likeNotification != null && user.getReceivedLikeNotifications().contains(likeNotification)) {
            likeNotificationRepository.delete(likeNotification);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/comments")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#userId == principal.getId()")
    public ResponseEntity<?> getCommentActivities(@PathVariable int userId) {
        User user = userService.getUserById(userId);

        List<CommentNotification> notifications = user.getReceivedCommentNotifications();
        notifications.sort(
                Comparator.comparing(CommentNotification::isRead).reversed()
                        .thenComparing(CommentNotification::getCreated).reversed());

        return ResponseEntity.ok(notificationResourceAssembler.toCollectionModel(notifications));
    }

    @GetMapping("/comments/{activityId}")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#userId == principal.getId()")
    public ResponseEntity<?> getCommentActivities(@PathVariable int userId, @PathVariable int activityId) {
        User user = userService.getUserById(userId);
        CommentNotification commentNotification = commentNotificationRepository.findById(activityId).orElse(null);

        if (commentNotification != null && user.getReceivedCommentNotifications().contains(commentNotification)) {
            return ResponseEntity.ok(notificationResourceAssembler.toModel(commentNotification));
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/comments/{activityId}/read")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#userId == principal.getId()")
    public ResponseEntity<?> readCommentActivities(@PathVariable int userId, @PathVariable int activityId) {
        User user = userService.getUserById(userId);
        CommentNotification commentNotification = commentNotificationRepository.findById(activityId).orElse(null);

        if (commentNotification != null && user.getReceivedCommentNotifications().contains(commentNotification)) {
            commentNotification.setRead(!commentNotification.isRead());
            commentNotificationRepository.save(commentNotification);

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/comments/{activityId}/delete")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#userId == principal.getId()")
    public ResponseEntity<?> deleteCommentActivities(@PathVariable int userId, @PathVariable int activityId) {
        User user = userService.getUserById(userId);
        CommentNotification commentNotification = commentNotificationRepository.findById(activityId).orElse(null);

        if (commentNotification != null && user.getReceivedCommentNotifications().contains(commentNotification)) {
            commentNotificationRepository.delete(commentNotification);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/interests")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#userId == principal.getId()")
    public ResponseEntity<?> getInterestActivities(@PathVariable int userId) {
        User user = userService.getUserById(userId);

        List<InterestNotification> notifications = user.getReceivedInterestNotifications();
        notifications.sort(
                Comparator.comparing(InterestNotification::isRead).reversed()
                        .thenComparing(InterestNotification::getCreated).reversed());

        return ResponseEntity.ok(notificationResourceAssembler.toCollectionModel(notifications));
    }

    @GetMapping("/interests/{activityId}")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#userId == principal.getId()")
    public ResponseEntity<?> getInterestActivities(@PathVariable int userId, @PathVariable int activityId) {
        User user = userService.getUserById(userId);
        InterestNotification interestNotification = interestNotificationRepository.findById(activityId).orElse(null);

        if (interestNotification != null && user.getReceivedInterestNotifications().contains(interestNotification)) {
            return ResponseEntity.ok(notificationResourceAssembler.toModel(interestNotification));
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/interests/{activityId}/read")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#userId == principal.getId()")
    public ResponseEntity<?> readInterestActivities(@PathVariable int userId, @PathVariable int activityId) {
        User user = userService.getUserById(userId);
        InterestNotification interestNotification = interestNotificationRepository.findById(activityId).orElse(null);

        if (interestNotification != null && user.getReceivedInterestNotifications().contains(interestNotification)) {
            interestNotification.setRead(!interestNotification.isRead());
            interestNotificationRepository.save(interestNotification);

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/interests/{activityId}/delete")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#userId == principal.getId()")
    public ResponseEntity<?> deleteInterestActivities(@PathVariable int userId, @PathVariable int activityId) {
        User user = userService.getUserById(userId);
        InterestNotification interestNotification = interestNotificationRepository.findById(activityId).orElse(null);

        if (interestNotification != null && user.getReceivedInterestNotifications().contains(interestNotification)) {
            interestNotificationRepository.delete(interestNotification);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
