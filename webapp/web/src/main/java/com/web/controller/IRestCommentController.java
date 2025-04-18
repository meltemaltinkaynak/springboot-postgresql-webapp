package com.web.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;

import com.web.dto.DtoComment;
import com.web.dto.DtoCommentIU;
import com.web.dto.DtoContentComment;
import com.web.dto.DtoUserComment;
import com.web.dto.DtoUserCommentList;
import com.web.dto.RootEntity;

import jakarta.servlet.http.HttpServletRequest;

public interface IRestCommentController {

		// Tüm yorumları getirme
		public RootEntity<List<DtoComment>>  getAllComments();
		
		// kullanıcı id ile yorumları getirme
		public RootEntity<List<DtoUserCommentList>>getCommentsByUserI(HttpServletRequest request);
		
		// Yorum id'si ile yorum getirme
		public RootEntity<DtoComment> getOneComment(Long commentId);
		
		// Content id ile yorum getirme
		public RootEntity<DtoContentComment> getComment(Long contentId);
		
		// Yorum oluşturma
		public RootEntity<DtoComment> saveComment( Long contentId, DtoCommentIU dtoCommentIU,HttpServletRequest request);
		

		//Yorum silme
		public void deleteComment(Long userId, Long commentId);
		
		// İçerik id içe yorum getirme
		public RootEntity<List<DtoComment>> getCommentsByContentId( Long contentId,HttpServletRequest request);
		
		//Content id ile yorum sayısı getirme
		public RootEntity<Object> getCommentCountByContentId(Long contentId,  HttpServletRequest request);
		
		//Content id ile yorum getirme public
		public RootEntity<List<DtoComment>> getCommentsByContentId(@PathVariable Long contentId);
		
		//Content id ile yorum sayısı getirme public
		public RootEntity<Object> getCommentCountByContentId(@PathVariable Long contentId);
		
}
