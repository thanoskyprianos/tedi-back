package com.network.network.comment.resource;

import com.network.network.comment.Comment;
import com.network.network.comment.CommentController;
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
public class CommentResourceAssembler implements RepresentationModelAssembler<Comment, EntityModel<Comment>> {
    @Resource
    private UserService userService;

    @Override @NonNull
    public EntityModel<Comment> toModel(@NonNull Comment entity) {
        User user = userService.getPrincipal();

        EntityModel<Comment> model = EntityModel.of(entity,
            linkTo(methodOn(CommentController.class).getComment(
                    entity.getPost().getUser().getId(),
                    entity.getPost().getId(),
                    entity.getId())).withSelfRel()
        );

        if (entity.getUser().getId() == user.getId()) {
            model.add(
                linkTo(methodOn(CommentController.class).deleteComment(
                    entity.getPost().getUser().getId(),
                    entity.getPost().getId(),
                    entity.getId())).withRel("delete")
            );
        }

        model.add(
                linkTo(methodOn(UserController.class).getUser(
                        entity.getUser().getId())).withRel("author"),
                linkTo(methodOn(PostController.class).getUserPost(
                        entity.getPost().getUser().getId(),
                        entity.getPost().getId())).withRel("post")
        );

        return model;
    }
}
