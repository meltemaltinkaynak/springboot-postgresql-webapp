package com.web.repository;

import org.springframework.data.domain.Pageable; 
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.web.dto.DtoContent;
import com.web.dto.DtoPopular;
import com.web.model.Content;
import com.web.model.ContentCategory;
import com.web.model.User;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long>{
	
	List<Content> findByCategory(ContentCategory category);
	
	List<Content> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime createdAt);
	 
	
	List<Content> findByCategoryOrderByCreatedAtDesc(ContentCategory category);

	//  kısıtı içerikleri kategoriye göre sıralı şekilde al
	List<Content> findByCategoryAndIsRestrictedOrderByCreatedAtDesc(ContentCategory category, boolean isRestricted);
	
	// Görüntülenme sayısına göre ilk 3
	List<Content> findTop3ByOrderByViewCountDesc();
	
	// son bir hafta public
	List<Content> findByCreatedAtAfterAndIsRestrictedOrderByCreatedAtDesc(LocalDateTime createdAt, boolean isRestricted);

	
	// Kullanıcı içerikleri
	public List<Content> findByUserIdOrderByCreatedAtDesc(Long userId);
	
	// kısıtsız içerikler
	 Optional<Content> findByIdAndIsRestrictedFalse(Long contentId); // Kısıtlı olmayan içeriği ID ile getir
	
	 
	 
	
	 long countByUser(User user);
	 long countByUserAndIsRestricted(User user, boolean isRestricted);

	 @Query("SELECT c FROM Content c " +
		       "LEFT JOIN Like l ON l.content = c " +
		       "LEFT JOIN Comment cm ON cm.content = c " +
		       "WHERE c.user = :user " +
		       "GROUP BY c " +
		       "ORDER BY (COUNT(l) + COUNT(cm) + c.viewCount) DESC")
		List<Content> findMostInteractedContent(@Param("user") User user);

	   List<Content> findByUser(User user); // Bu satırı ekle

	
	// son 4 içerik
	List<Content> findTop4ByOrderByCreatedAtDesc();
	
	//web statistics
	@Query("SELECT SUM(c.viewCount) FROM Content c")
    Long sumViewCounts();
	 
	// Popüler
	 @Query("SELECT c FROM Content c LEFT JOIN c.likes l LEFT JOIN c.comments cm GROUP BY c.id ORDER BY (COUNT(l) + COUNT(cm) + c.viewCount) DESC")
	    List<Content> findTop10Popular(Pageable pageable);

	    @Query("SELECT c FROM Content c LEFT JOIN c.likes l GROUP BY c.id ORDER BY COUNT(l) DESC")
	    List<Content> findTop10ByLikes(Pageable pageable);

	    @Query("SELECT c FROM Content c LEFT JOIN c.comments cm GROUP BY c.id ORDER BY COUNT(cm) DESC")
	    List<Content> findTop10ByComments(Pageable pageable);

	    @Query("SELECT c FROM Content c ORDER BY c.viewCount DESC")
	    List<Content> findTop10ByViews(Pageable pageable);
	

	 
	


}
