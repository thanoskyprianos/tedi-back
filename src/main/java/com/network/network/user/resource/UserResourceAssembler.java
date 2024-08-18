package com.network.network.user.resource;

import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.media.UserMediaController;
import com.network.network.misc.View;
import com.network.network.post.PostController;
import com.network.network.user.User;
import com.network.network.user.UserController;
import com.network.network.user.info.InfoController;
import lombok.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserResourceAssembler implements RepresentationModelAssembler<User, EntityModel<User>> {

    @Override @NonNull
    @JsonView(View.AsProfessional.class)
    public EntityModel<User> toModel(@NonNull User entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(UserController.class).getUser(entity.getId())).withSelfRel(),
                linkTo(methodOn(UserMediaController.class).getUserAvatar(entity.getId())).withRel("avatar"),
                linkTo(methodOn(InfoController.class).getInfo(entity.getId())).withRel("info"),
                linkTo(methodOn(PostController.class).getUserPosts(entity.getId())).withRel("posts")
        );
    }
}
