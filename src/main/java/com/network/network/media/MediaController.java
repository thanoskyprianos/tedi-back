package com.network.network.media;

import jakarta.annotation.Resource;
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

    @PostMapping("/upload/single")
    public ResponseEntity<?> saveFile(@RequestParam MultipartFile media) {
        // change it to created
        return ResponseEntity.ok(mediaService.saveFile(media));
    }

    @PostMapping("/upload/multiple")
    public ResponseEntity<?> saveFile(@RequestParam List<MultipartFile> media) {
        // change it to created
        return ResponseEntity.ok(mediaService.saveFiles(media));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateFile(@PathVariable int id, @RequestParam MultipartFile media) {
        return ResponseEntity.ok(mediaService.updateFile(id, media));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable int id) {
        mediaService.deleteFile(id);

        return ResponseEntity.ok(Map.of("message", "Image " + id + " deleted"));
    }
}
