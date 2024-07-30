package com.network.network.media;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MediaService {
    @Value("${media.path}")
    private String mediaPath;

    @Resource
    private MediaRepository mediaRepository;

    private final SimpleDateFormat sdf =
            new SimpleDateFormat("yyyyMMdd'at'HHmmssSSz");

    public Media getMedia(int id) {
        return mediaRepository.findById(id).orElse(null);
    }

    public Media saveFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyMediaException();
        }

        String fileName =
                file.getName() +
                file.getSize() +
                file.getResource().hashCode() +
                sdf.format(new Date()) +
                file.getOriginalFilename();

        String type = file
                .getContentType()
                .substring(0, file.getContentType().indexOf('/'));

        Path filePath = Path.of(mediaPath, type, fileName);

        try {
            file.transferTo(filePath);
        } catch (IOException e) {
            throw new MediaSavingException(file.getOriginalFilename());
        }

        Media media = new Media(filePath.toString(), file.getContentType());
        return mediaRepository.save(media);
    }

    @Transactional
    public List<Media> saveFiles(List<MultipartFile> files) {
        List<Media> mediaList = new ArrayList<>();

        for (MultipartFile file : files) {
            mediaList.add(saveFile(file));
        }

        return mediaList;
    }
}