package com.network.network.media.resource;

import com.network.network.media.Media;
import lombok.NonNull;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class MediaResourceAssembler implements RepresentationModelAssembler<Media, EntityModel<Media>> {

    @Override @NonNull
    public EntityModel<Media> toModel(@NonNull Media entity) {
        return EntityModel.of(entity
//                ,
//                linkTo(methodOn(UserMediaController.class).getMedia(entity.getId())).withSelfRel(),
//                linkTo(methodOn(UserMediaController.class).updateFile(entity.getId(), null)).withRel("update"),
//                linkTo(methodOn(UserMediaController.class).deleteFile(entity.getId())).withRel("delete")
        );
    }

    @Override @NonNull
    public CollectionModel<EntityModel<Media>> toCollectionModel(@NonNull Iterable<? extends Media> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
