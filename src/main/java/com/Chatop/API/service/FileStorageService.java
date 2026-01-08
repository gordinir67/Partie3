package com.Chatop.API.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.public-base-url:/images}")
    private String publicBaseUrl;

    @Value("${app.public.base-url:http://localhost:3001}")
    private String publicBaseUrlHost;

    public String store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Empty file");
        }

        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(dir);

        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0 && dot < original.length() - 1) {
            ext = original.substring(dot);
        }
        String filename = UUID.randomUUID() + ext;

        Path target = dir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return publicBaseUrlHost + publicBaseUrl + "/" + filename;
    }
}
