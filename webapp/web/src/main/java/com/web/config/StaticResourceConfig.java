package com.web.config;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
	
	@Value("${file.upload-dir.public}")
    private String publicUploadDir;

    @Value("${file.upload-dir.private}")
    private String privateUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	registry.addResourceHandler("/uploads/public/**")
        .addResourceLocations("file:" + publicUploadDir + "/");

    	registry.addResourceHandler("/uploads/private/**")
        .addResourceLocations("file:" + privateUploadDir + "/");

    }

}