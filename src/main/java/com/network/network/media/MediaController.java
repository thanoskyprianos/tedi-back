package com.network.network.media;

import jakarta.annotation.Resource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/media")
@CrossOrigin("*")
public class MediaController {
    @Resource
    private MediaService mediaService;

    @Resource
    private MediaResourceAssembler mediaResourceAssembler;

    @GetMapping("/{id}")
    public ResponseEntity<?> getMedia(@PathVariable int id) {
        Media media = mediaService.getMedia(id);

        return ResponseEntity.ok(mediaResourceAssembler.toModel(media));
    }

    @PostMapping("/upload/single")
    public ResponseEntity<?> saveFile(@RequestParam MultipartFile media) {
        Media mediaSaved = mediaService.saveFile(media);
        EntityModel<Media> entityModel = mediaResourceAssembler.toModel(mediaSaved);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PostMapping("/upload/multiple")
    public ResponseEntity<?> saveFile(@RequestParam List<MultipartFile> media) {
        List<Media> mediaSaved = mediaService.saveFiles(media);
        CollectionModel<EntityModel<Media>> entityModel = mediaResourceAssembler.toCollectionModel(mediaSaved);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(entityModel);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateFile(@PathVariable int id, @RequestParam MultipartFile media) {
        return ResponseEntity
                .ok(mediaResourceAssembler
                        .toModel(mediaService.updateFile(id, media)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable int id) {
        mediaService.deleteFile(id);

        return ResponseEntity.ok(Map.of("message", "Image " + id + " deleted"));
    }
}
