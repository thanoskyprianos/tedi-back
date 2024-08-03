package com.network.network.user.resource;

import com.network.network.media.UserMediaController;
import com.network.network.user.User;
import com.network.network.user.UserController;
import lombok.NonNull;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserResourceAssembler implements RepresentationModelAssembler<User, EntityModel<User>> {

    @Override @NonNull
    public EntityModel<User> toModel(@NonNull User entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(UserController.class).getUser(entity.getId())).withSelfRel(),
                linkTo(methodOn(UserMediaController.class).getUserAvatar(entity.getId())).withRel("avatar")
        );
    }

    @Override @NonNull
    public CollectionModel<EntityModel<User>> toCollectionModel(@NonNull Iterable<? extends User> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
