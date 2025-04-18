package com.web.service;

import java.util.List;

import com.web.dto.DtoComment;
import com.web.dto.DtoCommentIU;
import com.web.dto.DtoContentComment;
import com.web.dto.DtoUserComment;
import com.web.dto.DtoUserCommentList;
import com.web.dto.RootEntity;




public interface ICommentService {

	// Tüm yorumları getirme
	public List<DtoComment> getAllComments();
	
	// kullanıcı id'ai ile yorumları getirme
		
	public List<DtoUserCommentList> getCommentsByUserId(Long userId);
	
	// Yorum id'si ile yorum getirme
	public DtoComment getOneComment(Long commentId);
	
	
	// Content id ile yorum getirme
	public DtoContentComment getComment(Long contentId);
	
	// Yorum oluşturma
	public DtoComment saveComment( Long contentId,Long userId, DtoCommentIU dtoCommentIU);
	
	
	//Yorum silme
	public void deleteComment(Long userId, Long commentId);
	
	
	//Content id ile yorum getirme
	public List<DtoComment> getCommentsByContentId(Long contentId);
	
	//Content id yorum sayısı
	public int getCommentCountByContentId(Long contentId);
}
