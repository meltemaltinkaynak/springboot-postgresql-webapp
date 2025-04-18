package com.web.service.Impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;


@Service
public class FileStorageServiceImpl {
	
	@Value("${file.upload-dir.public}")
    private String publicUploadDir;  // Public dizini için upload yolunu al

    @Value("${file.upload-dir.private}")
    private String privateUploadDir;  // Private dizini için upload yolunu al

    public String storeFile(MultipartFile file, boolean isRestricted) {
        try {
            // Dosya adı oluştur
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // Dosyayı hedef dizine kaydet
            Path targetLocation = Paths.get(isRestricted ? privateUploadDir : publicUploadDir).resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);

            // Dosya URL'sini döndür
            return "/uploads/" + (isRestricted ? "private/" : "public/") + fileName;  // Dosya yolu, frontend'e dönecek URL
        } catch (IOException e) {
            throw new RuntimeException("Fotoğraf yüklenirken bir hata oluştu", e);
        }
    }

    @PostConstruct
    public void init() {
        File publicDir = new File(publicUploadDir);
        File privateDir = new File(privateUploadDir);

        // Eğer dizinler yoksa, oluştur
        if (!publicDir.exists()) {
            publicDir.mkdirs();
        }
        if (!privateDir.exists()) {
            privateDir.mkdirs();
        }
    }
	    
	  
	    
	    public String ppstoreFileFromBase64(String base64String) {
	        try {
	            // MIME tipini al
	            String mimeType = base64String.substring(base64String.indexOf(":") + 1, base64String.indexOf(";"));
	            String extension = switch (mimeType) {
	                case "image/png" -> "png";
	                case "image/jpeg", "image/jpg" -> "jpg";
	                case "image/gif" -> "gif";
	                default -> throw new RuntimeException("Geçersiz dosya formatı!");
	            };

	            // Base64 verisini çöz
	            byte[] decodedBytes = Base64.getDecoder().decode(base64String.split(",")[1]);

	            // Dosya adını oluştur
	            String fileName = System.currentTimeMillis() + "." + extension;

	            // Dosyayı private dizine kaydet
	            Path targetLocation = Paths.get(privateUploadDir).resolve(fileName);
	            Files.write(targetLocation, decodedBytes);

	            return "/uploads/private/" + fileName;  // Fotoğrafın private dizinine kaydedildiğini belirtiyoruz
	        } catch (IOException e) {
	            throw new RuntimeException("Base64 fotoğraf yüklenirken bir hata oluştu", e);
	        }
	    }

	    
	    


}
