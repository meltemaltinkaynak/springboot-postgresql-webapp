package com.web.dto;

import java.time.LocalDateTime;

import com.web.model.ContentCategory;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoContentIU {
	
	@NotEmpty(message = "Başlık alanı boş bırakılamaz!")
	@Size(min =1, max = 1000,  message="Başlık maximum 1000 karakter olmalı.")
	private String title;
	
	@NotEmpty(message = "Resim alanı boş bırakılamaz!")
	private String photo;
	
	@NotEmpty(message = "Metin alanı boş bırakılamaz!")
	private String text;
	
	@NotEmpty(message = "Kategori alanı boş bırakılamaz!")
	private ContentCategory category;
	
	

}
