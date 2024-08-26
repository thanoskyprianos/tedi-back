package com.network.network.connections.resource;

import com.network.network.connections.ConnectionController;
import com.network.network.connections.ConnectionNotification;
import com.network.network.user.User;
import com.network.network.user.UserController;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
import lombok.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ConnectionNotificationResourceAssembler implements RepresentationModelAssembler<ConnectionNotification, EntityModel<ConnectionNotification>> {
    @Resource
    private UserService userService;

    @Override
    public @NonNull EntityModel<ConnectionNotification> toModel(@NonNull ConnectionNotification entity) {
        User principal = userService.getPrincipal();

        EntityModel<ConnectionNotification> model = EntityModel.of(entity,
                linkTo(methodOn(UserController.class).getUser(entity.getReceiver().getId())).withRel("receiver"),
                linkTo(methodOn(UserController.class).getUser(entity.getSender().getId())).withRel("sender")
        );

        if (entity.getReceiver() == principal) {
            model.add(
                    linkTo(methodOn(ConnectionController.class)
                            .getReceived(principal.getId(), entity.getId())).withSelfRel(),
                    linkTo(methodOn(ConnectionController.class)
                            .acceptRequest(principal.getId(), entity.getId())).withRel("accept"),
                    linkTo(methodOn(ConnectionController.class)
                            .rejectRequest(principal.getId(), entity.getId())).withRel("reject")
            );
        }
        else if (entity.getSender() == principal) {
            model.add(
                    linkTo(methodOn(ConnectionController.class)
                            .getSent(principal.getId(), entity.getId())).withSelfRel(),
                    linkTo(methodOn(ConnectionController.class)
                            .cancelRequest(principal.getId(), entity.getId())).withRel("cancel")
            );
        }

        return model;
    }
}
