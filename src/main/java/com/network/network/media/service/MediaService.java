package com.network.network.media.service;

import com.network.network.media.Media;
import com.network.network.media.exception.EmptyMediaException;
import com.network.network.media.exception.MediaNotFoundException;
import com.network.network.media.exception.MediaSavingException;
import com.network.network.media.resource.MediaRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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

    private Media saveSingle(MultipartFile file) {
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

        return new Media(filePath.toString(), file.getContentType());
    }

    public Object fetchMedia(Media media) {
        try {
            return new UrlResource(new URI(media.getPath()));
        } catch (URISyntaxException | MalformedURLException e) {
            throw new MediaNotFoundException(media.getId());
        }
    }

    public Media saveFile(MultipartFile file) {
        return mediaRepository.save(saveSingle(file));
    }

    @Transactional
    public List<Media> saveFiles(List<MultipartFile> files) {
        List<Media> mediaList = new ArrayList<>();

        for (MultipartFile file : files) {
            mediaList.add(saveFile(file));
        }

        return mediaList;
    }

    public Media updateFile(int id, MultipartFile file) {
        Media media = mediaRepository.findById(id).orElseThrow(() -> new MediaNotFoundException(id));
        Media newFile = saveSingle(file);

        media.replaceMedia(newFile.getPath(), newFile.getContentType());

        return mediaRepository.save(media);
    }

    public void deleteFile(int id) {
        mediaRepository.findById(id).ifPresent(
                media -> mediaRepository.delete(media)
        );
    }
}
