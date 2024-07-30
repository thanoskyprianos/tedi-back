package com.network.network.media;

import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/media")
@CrossOrigin("*")
public class MediaController {
    @Resource
    private MediaService mediaService;

    @PostMapping("/upload/single")
    public ResponseEntity<?> saveFile(@RequestParam MultipartFile media) {
        return ResponseEntity.ok(mediaService.saveFile(media));
    }

    @PostMapping("/upload/multiple")
    public ResponseEntity<?> saveFile(@RequestParam List<MultipartFile> media) {
        return ResponseEntity.ok(mediaService.saveFiles(media));
    }

}
