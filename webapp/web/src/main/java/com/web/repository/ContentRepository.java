package com.web.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.web.dto.DtoContent;
import com.web.model.Content;
import com.web.model.ContentCategory;
import com.web.model.User;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long>{
	
	List<Content> findByCategory(ContentCategory category);
	
	List<Content> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime createdAt);
	 
	
	List<Content> findByCategoryOrderByCreatedAtDesc(ContentCategory category);

	// WEBTASARIM kategorisinde ve kısıtlamalı içerikleri sıralı şekilde al
	List<Content> findByCategoryAndIsRestrictedOrderByCreatedAtDesc(ContentCategory category, boolean isRestricted);
	
	// Kullanıcı içerikleri
	public List<Content> findByUserIdOrderByCreatedAtDesc(Long userId);
	 
	 
	
	 long countByUser(Optional<User> user);
	 long countByUserAndIsRestricted(Optional<User> user, boolean isRestricted);
	 Content findMostInteractedContent(Optional<User> user);
	

}
