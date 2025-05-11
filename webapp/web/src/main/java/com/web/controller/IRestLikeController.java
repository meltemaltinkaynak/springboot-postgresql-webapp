package com.web.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.web.dto.DtoCommentIU;
import com.web.dto.DtoLike;
import com.web.dto.DtoLikeIU;
import com.web.dto.DtoUserLike;
import com.web.dto.RootEntity;

import jakarta.servlet.http.HttpServletRequest;

public interface IRestLikeController {
	
	// Tüm likeları getirme
	public RootEntity<List<DtoLike>> getAllLikes();
	
	// User id  ile like getirme
	 public RootEntity<List<DtoUserLike>> getLikesByUserId(HttpServletRequest request);
	

	// like id  ile like getirme
	public RootEntity<DtoLike> getOneLike(Long likeId);
	
	// Like oluşturma
//	public RootEntity<DtoLike> saveLike(Long contentId,Long userId);
	public RootEntity<DtoLike> toggleLike(@PathVariable Long contentId, HttpServletRequest request);
	

	
	// içerik beğenileri
	public RootEntity<List<DtoLike>> getLikesByContentId(Long contentId,  HttpServletRequest request);
	
	// içerik beğenileri public
	public RootEntity<List<DtoLike>> getLikesByContentId(@PathVariable Long contentId);
	
	//Content id ile beğeni sayısı getirme
	public RootEntity<Object> getLikeCountByContentId(Long contentId,  HttpServletRequest request);
	
	//Content id ile beğeni sayısı getirme public
	public RootEntity<Object> getLikeCountByContentId(@PathVariable Long contentId);
	
	
	// kullanıcı ieçrik beğen durumu
	public RootEntity<Boolean> checkUserLikeStatus(@PathVariable Long contentId, HttpServletRequest request);
    
    
    
}
