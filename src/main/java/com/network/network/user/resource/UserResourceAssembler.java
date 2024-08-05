package com.network.network.user.resource;

import com.network.network.media.UserMediaController;
import com.network.network.user.User;
import com.network.network.user.UserController;
import com.network.network.user.repr.UserRepr;
import lombok.NonNull;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserResourceAssembler implements RepresentationModelAssembler<User, EntityModel<UserRepr>> {

    @Override @NonNull
    public EntityModel<UserRepr> toModel(@NonNull User entity) {
        return EntityModel.of(new UserRepr(entity),
                linkTo(methodOn(UserController.class).getUser(entity.getId())).withSelfRel(),
                linkTo(methodOn(UserMediaController.class).getUserAvatar(entity.getId())).withRel("avatar"),
                linkTo(methodOn(UserMediaController.class).getUserCV(entity.getId())).withRel("cv")
        );
    }

    @Override @NonNull
    public CollectionModel<EntityModel<UserRepr>> toCollectionModel(@NonNull Iterable<? extends User> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
