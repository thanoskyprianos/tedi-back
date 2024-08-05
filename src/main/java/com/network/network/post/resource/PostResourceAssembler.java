package com.network.network.post.resource;

import com.network.network.media.Media;
import com.network.network.media.PostMediaController;
import com.network.network.post.Post;
import com.network.network.post.PostController;
import lombok.NonNull;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PostResourceAssembler implements RepresentationModelAssembler<Post, EntityModel<Post>> {
    @Override @NonNull
    public EntityModel<Post> toModel(@NonNull Post entity) {
        EntityModel<Post> model = EntityModel.of(entity,
                linkTo(methodOn(PostController.class).post(entity.getUser().getId(), entity.getId())).withSelfRel(),
                linkTo(methodOn(PostController.class)
                        .deletePost(entity.getUser().getId(), entity.getId()))
                        .withRel("delete"),
                linkTo(methodOn(PostMediaController.class)
                        .addMedia(entity.getUser().getId(), entity.getId(), null))
                        .withRel("add_media")
        );

        int i = 0;
        for (Media _media : entity.getMediaList()) {
            model.add(linkTo(methodOn(PostMediaController.class)
                    .getMedia(entity.getUser().getId(), entity.getId(), i))
                    .withRel("media_" + i));
            i++;
        }

        return model;
    }

    @Override @NonNull
    public CollectionModel<EntityModel<Post>> toCollectionModel(@NonNull Iterable<? extends Post> entities) {
        return CollectionModel.of(IterableUtils.toList(entities).stream().map(this::toModel).toList());
    }
}
