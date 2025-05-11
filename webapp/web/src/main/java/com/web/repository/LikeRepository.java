package com.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.web.dto.DtoUserLike;
import com.web.model.Content;
import com.web.model.Like;
import com.web.model.User;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long>{



    // Belirli bir içeriğe ait beğenileri getir
    Optional<Like> findByUser_IdAndContent_Id(Long userId, Long contentId);


    // Belirli bir kullanıcının tüm beğenilerini getir
    List<Like> findByUser_Id(Long userId);

    // Kullanıcının belirli bir içeriğe bıraktığı beğeniyi getir (Opsiyonel olarak kullanabilirsin)
    boolean existsByUser_IdAndContent_Id(Long userId, Long contentId);

    // Kullanıcının belirli bir içeriğe yaptığı beğeniyi sil (Opsiyonel olarak kullanabilirsin)
    
    void deleteByUser_IdAndContent_Id(Long userId, Long contentId);
    
    
    // kullanıcı ieçrik beğen durumu
    boolean existsByContentIdAndUser_Id(Long contentId, Long userId);
    
    
    
    @Query("SELECT new com.web.dto.DtoUserLike(l.content.id, l.content.title, l.createdAt) " +
            "FROM Like l WHERE l.user.id = :userId")
     List<DtoUserLike> findLikesByUserId(Long userId);
    
    
    long countLikesByUser(User user);
    long countLikesByContent(Content content);
   
	
}
