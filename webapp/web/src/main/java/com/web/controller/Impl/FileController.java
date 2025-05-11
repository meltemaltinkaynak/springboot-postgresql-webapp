package com.web.controller.Impl;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/uploads")

public class FileController {

	  @Value("${file.upload-dir.private}")
	    private String privateUploadDir;

	    @GetMapping("/private/{fileName}")
	    @PreAuthorize("isAuthenticated()") // Sadece giriş yapmış kullanıcılar erişebilir
	    public ResponseEntity<byte[]> getPrivateFile(@PathVariable String fileName) {
	        try {
	            // Dosya yolunu oluştur
	            Path filePath = Paths.get(privateUploadDir).resolve(fileName);
	            File file = filePath.toFile();

	            // Dosya mevcut değilse 404 dön
	            if (!file.exists()) {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	            }

	            // Dosya içeriğini oku
	            byte[] fileContent = Files.readAllBytes(filePath);

	            // İçerik türünü belirle
	            String contentType = Files.probeContentType(filePath);
	            if (contentType == null) {
	                contentType = "application/octet-stream";
	            }

	            // HTTP başlıklarını ayarla
	            HttpHeaders headers = new HttpHeaders();
	            headers.setContentType(MediaType.parseMediaType(contentType));

	            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);

	        } catch (IOException e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	    }
}
