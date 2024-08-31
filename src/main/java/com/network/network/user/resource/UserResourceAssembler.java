package com.network.network.user.resource;

import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.connections.ConnectionController;
import com.network.network.connections.ConnectionNotification;
import com.network.network.connections.service.ConnectionService;
import com.network.network.media.UserMediaController;
import com.network.network.misc.View;
import com.network.network.notification.controllers.ActivityController;
import com.network.network.post.PostController;
import com.network.network.user.User;
import com.network.network.user.UserController;
import com.network.network.user.info.InfoController;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
import lombok.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserResourceAssembler implements RepresentationModelAssembler<User, EntityModel<User>> {
    @Resource
    private UserService userService;

    @Resource
    private ConnectionService connectionService;

    @Override @NonNull
    @JsonView(View.AsProfessional.class)
    public EntityModel<User> toModel(@NonNull User entity) {
        User principal = userService.getPrincipal();

        EntityModel<User> entityModel = EntityModel.of(entity,
                linkTo(methodOn(UserController.class).getUser(entity.getId())).withSelfRel(),
                linkTo(methodOn(UserMediaController.class).getUserAvatar(entity.getId())).withRel("avatar"),
                linkTo(methodOn(InfoController.class).getInfo(entity.getId())).withRel("info")
        );

        if (entity.getId() == principal.getId()) {
            entityModel.add(
                    linkTo(methodOn(PostController.class)
                            .getUserPosts(entity.getId())).withRel("posts"),
                    linkTo(methodOn(UserController.class)
                            .updateEmail(entity.getId(), null)).withRel("email"),
                    linkTo(methodOn(UserController.class)
                            .updatePassword(entity.getId(), null)).withRel("password"),
                    linkTo(methodOn(UserController.class)
                            .getConnections(entity.getId())).withRel("connections"),
                    linkTo(methodOn(ConnectionController.class)
                            .getSent(entity.getId())).withRel("sent")
            );

            // check if there are friend requests
            if (!entity.getReceived().isEmpty()) {
                entityModel.add(
                        linkTo(methodOn(ConnectionController.class)
                                .getReceived(entity.getId())).withRel("received")
                );
            }

            // check if there are like notifications
            if (!entity.getReceivedLikeNotifications().isEmpty()) {
                entityModel.add(
                        linkTo(methodOn(ActivityController.class)
                                .getLikeActivities(entity.getId())).withRel("like_activity")
                );
            }

            // check if there are comment notifications
            if (!entity.getReceivedCommentNotifications().isEmpty()) {
                entityModel.add(
                        linkTo(methodOn(ActivityController.class)
                                .getCommentActivities(entity.getId())).withRel("comment_activity")
                );
            }

            // check if there are job interest notifications
            if (!entity.getReceivedInterestNotifications().isEmpty()) {
                entityModel.add(
                        linkTo(methodOn(ActivityController.class)
                                .getInterestActivities(entity.getId())).withRel("interest_activity")
                );
            }

        } else if (principal.isConnected(entity)) {
            entityModel.add(
                    linkTo(methodOn(PostController.class)
                            .getUserPosts(entity.getId())).withRel("posts"),
                    linkTo(methodOn(UserController.class)
                            .getConnections(entity.getId())).withRel("connections"),
                    linkTo(methodOn(ConnectionController.class)
                            .removeFriend(principal.getId(), entity.getId())).withRel("remove")
            );
        } else {
            ConnectionNotification notification
                    = connectionService.getRequest(principal.getId(), entity.getId());

            if (notification != null) {
                if (notification.getReceiver() == principal) {
                    entityModel.add(
                            linkTo(methodOn(ConnectionController.class)
                                    .acceptRequest(principal.getId(), notification.getId())).withRel("accept"),
                            linkTo(methodOn(ConnectionController.class)
                                    .rejectRequest(principal.getId(), notification.getId())).withRel("reject")
                    );
                } else if (notification.getSender() == principal) {
                    entityModel.add(
                            linkTo(methodOn(ConnectionController.class)
                                    .cancelRequest(principal.getId(), notification.getId())).withRel("cancel"));
                }
            } else {
                entityModel.add(
                        linkTo(methodOn(ConnectionController.class)
                                .addFriend(principal.getId(), entity.getId())).withRel("add")
                );
            }

        }

        return entityModel;
    }
}
