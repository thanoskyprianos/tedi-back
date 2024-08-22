package com.network.network.post.resource;

import com.network.network.media.Media;
import com.network.network.media.PostMediaController;
import com.network.network.post.Post;
import com.network.network.post.PostController;
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
public class PostResourceAssembler implements RepresentationModelAssembler<Post, EntityModel<Post>> {
    @Resource
    private UserService userService;

    @Override @NonNull
    public EntityModel<Post> toModel(@NonNull Post entity) {
        User principal = userService.getPrincipal();

        EntityModel<Post> model = EntityModel.of(entity,
                linkTo(methodOn(PostController.class).getUserPost(
                        entity.getUser().getId(),
                        entity.getId())).withSelfRel(),

                linkTo(methodOn(PostController.class).likePost(entity.getUser().getId(),
                        entity.getId())).withRel("like"),

                linkTo(methodOn(PostController.class).likesForPost(
                        entity.getUser().getId(),
                        entity.getId())).withRel("likes"),

                linkTo(methodOn(PostController.class).commentPost(
                        entity.getUser().getId(),
                        entity.getId(),
                        null)).withRel("comment"),

                linkTo(methodOn(PostController.class).commentsForPost(
                        entity.getUser().getId(),
                        entity.getId())).withRel("comments"),
                linkTo(methodOn(UserController.class).getUser(entity.getUser().getId())).withRel("author")
        );

        // only if author requests post
        if (principal.getId() == entity.getUser().getId()) {
            model = model.add(
                linkTo(methodOn(PostController.class)
                    .deleteUserPost(entity.getUser().getId(), entity.getId()))
                    .withRel("delete"),
                linkTo(methodOn(PostMediaController.class)
                    .addMedia(entity.getUser().getId(), entity.getId(), null))
                    .withRel("add_media")
            );
        }

        int i = 0;
        for (Media _media : entity.getMediaList()) {
            model.add(linkTo(methodOn(PostMediaController.class)
                    .getMedia(entity.getUser().getId(), entity.getId(), i))
                    .withRel("media"));
            i++;
        }

        return model;
    }
}
