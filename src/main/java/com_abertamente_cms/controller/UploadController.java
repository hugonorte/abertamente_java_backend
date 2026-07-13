package com_abertamente_cms.controller;

import com_abertamente_cms.service.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final FileStorageService fileStorageService;

    // png, jpg/jpeg, webp, avif
    private static final List<String> ALLOWED_EXTENSIONS = List.of("image/png", "image/jpeg", "image/webp", "image/avif");

    public UploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("type") String type) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Nenhum arquivo enviado."));
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_EXTENSIONS.contains(contentType.toLowerCase())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Formato de arquivo não suportado. Use PNG, JPG, WEBP ou AVIF."));
        }

        long maxSize;
        String directory;

        if ("avatar".equalsIgnoreCase(type)) {
            maxSize = 1 * 1024 * 1024; // 1 MB
            directory = "avatars";
        } else if ("post".equalsIgnoreCase(type)) {
            maxSize = 2 * 1024 * 1024; // 2 MB
            directory = "posts";
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Tipo de upload inválido. Use 'avatar' ou 'post'."));
        }

        if (file.getSize() > maxSize) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(Map.of("message", "O arquivo excede o limite máximo permitido de " + (maxSize / 1024 / 1024) + "MB para " + type + "."));
        }

        String path = fileStorageService.storeFile(file, directory);

        return ResponseEntity.ok(Map.of("path", path));
    }
}
