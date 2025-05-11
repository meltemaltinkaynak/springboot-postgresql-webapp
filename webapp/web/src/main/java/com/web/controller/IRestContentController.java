package com.web.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.web.dto.DtoContent;
import com.web.dto.DtoContentIU;
import com.web.dto.DtoMostViewedContent;
import com.web.dto.DtoPopular;
import com.web.dto.DtoUserContent;
import com.web.dto.DtoUserStatistics;
import com.web.dto.RootEntity;
import com.web.model.ContentCategory;

import jakarta.servlet.http.HttpServletRequest;

public interface IRestContentController {
	
	// istatistik
	public RootEntity<DtoUserStatistics> getUserStatistics(HttpServletRequest request);

	// Tüm içerikleri getirme
	public RootEntity<List<DtoContent>> getAllContents();
		
	// Kullanıcı id'si ile içerik getirme
	public RootEntity<List<DtoContent>> getContentsByUserId(HttpServletRequest request);
	
	// içerik id'si ile içerik getirme
	public RootEntity<DtoContent>  getContentByContentId(@PathVariable Long contentId, HttpServletRequest request);
	
	
	// Görüntülenme sayısına göre ilk 3
	public RootEntity<List<DtoMostViewedContent>> getTop3MostViewedContent();
	// içerik id'si ile içerik getirme- misafir kullanıcı
	public RootEntity<DtoContent> getContentByContentIdForGuest(Long contentId);
		
	// İçerik oluşturma
	 public RootEntity<String> saveContent(
	            @RequestParam Map<String, String> contentData,
	            @RequestParam(required = false) MultipartFile image,
	            @RequestParam(required = false) boolean isRestricted,  // Bu parametreyi al
	            HttpServletRequest request);
	// İçerik güncelleme
//	public RootEntity<DtoContent > updateContent(Long userId, Long contentId, DtoContentIU dtoContentIU);
	public RootEntity<Map<String, Object>> patchContent(Long userId, Long contentId, Map<String, Object> updates);
	
	// İçerik silme
	public void deleteContent(Long userId, Long contentId);
	
	// içerik kısıt
	public boolean updateContentRestriction(Long contentId, boolean isRestricted, Long userId);
	
	// kategoriye göre içerik getirme
	public RootEntity<List<DtoContent>> getContentsByCategory(ContentCategory category); 
//	
//	// son bir haftanın içerikleri
	public RootEntity<List<DtoContent>> getRecentContents(HttpServletRequest request);
	//  son bir haftanın içerikleri - public- kısıtsız
	 public RootEntity<List<DtoContent>>  getRecentUnrestrictedContents();
	 
	 // son 4
	 public RootEntity<List<DtoContent>>  getRecentFourContentsContents();
	
	// Herkese açık kısıtsız  webtasrarımı içerikleri
	public RootEntity<List<DtoContent>> getWebDesignPublicContents();
	  
	// Giriş yapmış kullanıcıya webtasarımı tüm içerikler	
	public RootEntity<List<DtoContent>> getAllWebDesignContents(HttpServletRequest request);
	
	public RootEntity<List<DtoContent>> getHtmlPublicContents();
	public RootEntity<List<DtoContent>> getAllHtmlContents(HttpServletRequest request);
	
	public RootEntity<List<DtoContent>> getCSSPublicContents();
	public RootEntity<List<DtoContent>> getAllCSSContents(HttpServletRequest request);
	
	public RootEntity<List<DtoContent>> getJavaScriptPublicContents();
	public RootEntity<List<DtoContent>> getAllJavaScriptContents(HttpServletRequest request);
	
	
	public RootEntity<List<DtoContent>> getVeritabaniPublicContents();
	public RootEntity<List<DtoContent>> getAllVeritabaniContents(HttpServletRequest request);
	
	public RootEntity<List<DtoContent>> getbackendteknolojileriPublicContents();
	public RootEntity<List<DtoContent>> getAllbackendteknolojileriContents(HttpServletRequest request);
	
	
	public RootEntity<List<DtoContent>> getAPIPublicContents();
	public RootEntity<List<DtoContent>> getAllAPIContents(HttpServletRequest request) ;
	
	
	public RootEntity<List<DtoContent>> getHostingPublicContents();
	public RootEntity<List<DtoContent>> getAllHostingContents(HttpServletRequest request);
	
	public RootEntity<List<DtoContent>> getUIUXPublicContents();
	public RootEntity<List<DtoContent>> getAllUIUXContents(HttpServletRequest request);
	
	public RootEntity<List<DtoContent>> getTasarimAraclariPublicContents();
	public RootEntity<List<DtoContent>> getTasarimAraclariContents(HttpServletRequest request);
	
	public RootEntity<List<DtoContent>> getWebPerformansSeoPublicContents();
	public RootEntity<List<DtoContent>> getWebPerformansSeoContents(HttpServletRequest request);
	 
	
	public RootEntity<List<DtoContent>> getWebGuvenlikPublicContents();
	public RootEntity<List<DtoContent>> getWebGuvenlikContents(HttpServletRequest request);
	
	
	public RootEntity<List<DtoPopular>> getTop10Popular();
	public RootEntity<List<DtoPopular>> getTop10ByLikes();
	public RootEntity<List<DtoPopular>> getTop10ByComments();
	public RootEntity<List<DtoPopular>> getTop10ByViews();
	
	public RootEntity<List<DtoPopular>> getTop10Popular(HttpServletRequest request);
	public RootEntity<List<DtoPopular>> getTop10ByLikes(HttpServletRequest request);
	public RootEntity<List<DtoPopular>> getTop10ByComments(HttpServletRequest request);
	public RootEntity<List<DtoPopular>> getTop10ByViews(HttpServletRequest request);
	 
	public RootEntity<List<DtoContent>>  getRecentFourContentsContents(HttpServletRequest request);

	
	
}
