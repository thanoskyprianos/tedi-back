package com.network.network.notification.resource;

import com.network.network.notification.controllers.ActivityController;
import com.network.network.notification.modules.CommentNotification;
import com.network.network.notification.modules.Notification;
import com.network.network.notification.modules.NotificationType;
import com.network.network.post.PostController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class NotificationResourceAssembler implements RepresentationModelAssembler<Notification, EntityModel<Notification>> {
    @Override
    public EntityModel<Notification> toModel(Notification entity) {
        EntityModel<Notification> model = EntityModel.of(entity);

        if (entity.getNotificationType() == NotificationType.LIKE) {
            model.add(
                    linkTo(methodOn(ActivityController.class).getLikeActivities(entity.getReceiver().getId(), entity.getId())).withSelfRel(),
                    linkTo(methodOn(ActivityController.class).deleteLikeActivities(entity.getReceiver().getId(), entity.getId())).withRel("delete")
            );

            if (entity.isRead()) {
                model.add(linkTo(methodOn(ActivityController.class).readLikeActivities(entity.getReceiver().getId(), entity.getId())).withRel("unread"));
            } else {
                model.add(linkTo(methodOn(ActivityController.class).readLikeActivities(entity.getReceiver().getId(), entity.getId())).withRel("read"));
            }
        } else if (entity.getNotificationType() == NotificationType.COMMENT) {
            model.add(
                    linkTo(methodOn(ActivityController.class).getCommentActivities(entity.getReceiver().getId(), entity.getId())).withSelfRel(),
                    linkTo(methodOn(ActivityController.class).deleteCommentActivities(entity.getReceiver().getId(), entity.getId())).withRel("delete"),
                    linkTo(methodOn(PostController.class).getUserPost(
                            entity.getReceiver().getId(),
                            ((CommentNotification) entity).getComment().getPost().getId())).withRel("post")
            );

            if (entity.isRead()) {
                model.add(linkTo(methodOn(ActivityController.class).readCommentActivities(entity.getReceiver().getId(), entity.getId())).withRel("unread"));
            } else {
                model.add(linkTo(methodOn(ActivityController.class).readCommentActivities(entity.getReceiver().getId(), entity.getId())).withRel("read"));
            }
        } else if (entity.getNotificationType() == NotificationType.INTEREST) {
            model.add(
                    linkTo(methodOn(ActivityController.class).getInterestActivities(entity.getReceiver().getId(), entity.getId())).withSelfRel(),
                    linkTo(methodOn(ActivityController.class).deleteInterestActivities(entity.getReceiver().getId(), entity.getId())).withRel("delete")
            );

            if (entity.isRead()) {
                model.add(linkTo(methodOn(ActivityController.class).readInterestActivities(entity.getReceiver().getId(), entity.getId())).withRel("unread"));
            } else {
                model.add(linkTo(methodOn(ActivityController.class).readInterestActivities(entity.getReceiver().getId(), entity.getId())).withRel("read"));
            }
        }

        return model;
    }
}
