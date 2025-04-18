package com.web.service;

import java.util.List;

import com.web.dto.DtoCommentIU;
import com.web.dto.DtoLike;
import com.web.dto.DtoLikeIU;
import com.web.dto.DtoUserLike;




public interface ILikeService {
	
	// Tüm likeları getirme
	public List<DtoLike> getAllLikes();
	
	// userId ile like getirme
	List<DtoUserLike> getLikesByUserId(Long userId);
	
	
	// like id  ile like getirme
	public DtoLike getOneLike(Long likeId);
	
	// Like oluşturma
	public DtoLike toggleLike(Long contentId,Long userId);


	
	
	
	//içerik id ile beğenileri getirme	
	public List<DtoLike> getLikesByContentId(Long contentId);
	
	
	//begeni sayısı
	public int getLikeCountByContentId(Long contentId);
	
	
	// kullanıcı ieçrik beğen durumu
	public boolean isContentLikedByUser(Long contentId, Long userId);
}
