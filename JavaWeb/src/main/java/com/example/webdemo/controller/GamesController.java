package com.example.webdemo.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;

@Controller
@RequestMapping("/games")
public class GamesController {
    
    private static final Logger logger = LoggerFactory.getLogger(GamesController.class);

    @GetMapping
    public String gamesPage() {
        return "games";
    }

    @GetMapping("/download/game-client")
    public ResponseEntity<Resource> downloadGameClient() {
        try {
            // Çalışma dizinini logla
            logger.info("Çalışma dizini: {}", new File("").getAbsolutePath());
            // Mutlak dosya yolunu kullan
            String filePath = new File("").getAbsolutePath() + File.separator + "JavaProje.zip";
            logger.info("Dosya indirme isteği alındı. Dosya yolu: {}", filePath);
            
            File file = new File(filePath);
            if (!file.exists()) {
                logger.error("Dosya bulunamadı: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(file);
            logger.info("Dosya bulundu. Boyut: {} bytes", file.length());
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"JavaProje.zip\"")
                .body(resource);
                
        } catch (Exception e) {
            logger.error("Dosya indirme hatası: ", e);
            return ResponseEntity.notFound().build();
        }
    }
} 