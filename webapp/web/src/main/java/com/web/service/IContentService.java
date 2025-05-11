package com.web.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.web.dto.DtoContent;
import com.web.dto.DtoContentIU;
import com.web.dto.DtoMostViewedContent;
import com.web.dto.DtoPopular;
import com.web.dto.DtoStatistics;
import com.web.dto.DtoUserContent;
import com.web.dto.DtoUserStatistics;
import com.web.model.Content;
import com.web.model.ContentCategory;

import jakarta.servlet.http.HttpServletRequest;

public interface IContentService {
	
	// Tüm içerikleri getirme
	public List<DtoContent> getAllContents();
	
	// kullanıcı id'sine göre içerik getirme
	public List<DtoContent> getContentsByUserId(Long userId);
	
	// içerik id'si ile içerik getirme
	public DtoContent getContentByContentId(Long contentId);
	
	// misafir kullanıcı içerik getirme
	public DtoContent getContentByContentIdForGuest(Long contentId);
	
	// Görüntülenme sayısına göre ilk 3
	public List<DtoMostViewedContent> getTop3MostViewedContent();

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
	
	// son bir haftanın içerikleri- kısıtlı
	public List<DtoContent>  getRecentContents();
	
	// son 4 içerik
	public List<DtoContent> getRecentFourContents();
	
	//görüntülenme sayısı artış
	public void incrementViewCount(Long contentId);
	
	// son bir haftanın içerikleri- public
	public List<DtoContent> getRecentUnrestrictedContents();
	
	// kategoriye göre tüm içerikler zaman sıralı
	public List<DtoContent> getContentsByCategoryAndNoRestriction(ContentCategory category);
	
	//kategoriye göre ve kısıtlamalı içerikleri sıralı şekilde 
	public List<DtoContent> getContentsByCategoryAndRestricted(ContentCategory category, boolean isRestricted);
	
	//user statictics
	 public DtoUserStatistics getUserStatistics(Long userId);
	 
	 public List<DtoPopular> getTop10Popular();
	 public List<DtoPopular> getTop10ByLikes();
	 public List<DtoPopular> getTop10ByComments();
	 public List<DtoPopular> getTop10ByViews();
	 
	 
	 public List<DtoPopular> getTop10PopularPrivate();
	 public List<DtoPopular> getTop10ByLikesPrivate();
	 public List<DtoPopular> getTop10ByCommentsPrivate();
	 public List<DtoPopular> getTop10ByViewsPrivate();
	 
	 public List<DtoContent> getRecentFourContentsPrivate();
	
}
