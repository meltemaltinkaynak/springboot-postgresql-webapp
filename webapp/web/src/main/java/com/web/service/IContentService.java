package com.web.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.web.dto.DtoContent;
import com.web.dto.DtoContentIU;
import com.web.dto.DtoUserContent;
import com.web.dto.DtoUserStatistics;
import com.web.model.Content;
import com.web.model.ContentCategory;

public interface IContentService {
	
	// Tüm içerikleri getirme
	public List<DtoContent> getAllContents();
	
	// kullanıcı id'sine göre içerik getirme
	public List<DtoContent> getContentsByUserId(Long userId);
	
	// içerik id'si ile içerik getirme
	DtoContent getContentByContentId(Long contentId);

	// İçerik oluşturma
	public DtoContent saveContent(Long userId, String title, String text, ContentCategory category, String imageUrl, boolean isRestricted);
	
	// İçerik güncelleme
//	public DtoContent updateContent(Long userId, Long contentId, DtoContentIU dtoContentIU);
	
	public Map<String, Object> patchContent(Long userId, Long contentId, Map<String, Object> updates);
	
	// İçerik silme
	public void deleteContent(Long userId, Long contentId);
	
	// içerik kısıt
	public boolean updateContentRestriction(Long contentId, boolean isRestricted, Long userId);
	
	
	// kategoriye göre içerik getirme
	public List<DtoContent> getContentsByCategory(ContentCategory category); 
	
	// son bir haftanın içerikleri
	public List<DtoContent>  getRecentContents();
	
	// kategoriye göre tüm içerikler zaman sıralı
	public List<DtoContent> getContentsByCategoryAndNoRestriction(ContentCategory category);
	
	//kategoriye göre ve kısıtlamalı içerikleri sıralı şekilde 
	public List<DtoContent> getContentsByCategoryAndRestricted(ContentCategory category, boolean isRestricted);
	
	
	 public DtoUserStatistics getUserStatistics(Long userId);

}
