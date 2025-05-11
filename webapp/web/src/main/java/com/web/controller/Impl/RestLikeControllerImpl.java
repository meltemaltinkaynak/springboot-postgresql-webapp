package com.web.controller.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.controller.IRestLikeController;
import com.web.dto.DtoComment;
import com.web.dto.DtoCommentIU;
import com.web.dto.DtoContent;
import com.web.dto.DtoLike;
import com.web.dto.DtoLikeIU;
import com.web.dto.DtoUserLike;
import com.web.dto.RootEntity;
import com.web.exception.BaseException;
import com.web.exception.ErrorMessage;
import com.web.exception.MessageType;
import com.web.model.Comment;
import com.web.model.Content;
import com.web.model.User;
import com.web.repository.LikeRepository;
import com.web.security.JwtTokenProvider;
import com.web.service.ILikeService;
import com.web.service.IUserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/like")
public class RestLikeControllerImpl extends RestBaseController implements IRestLikeController {
	
	@Autowired
	private  ILikeService likeService;
	
	@Autowired
    private JwtTokenProvider jwtTokenProvider;

	
	
	// Tüm likeları getirme
	@GetMapping
	@Override
	public RootEntity<List<DtoLike>> getAllLikes() {
		
		return ok(likeService.getAllLikes());
	}
	
	//userid ile like
	@Override
	@GetMapping("/like-list")
	    public RootEntity<List<DtoUserLike>> getLikesByUserId(HttpServletRequest request) {
		 
		 	// Çerezlerden JWT token'ı al
	        String token = extractJwtFromRequest(request);

	        // Token yoksa veya geçersizse hata dön
	        if (token == null || !jwtTokenProvider.validateToken(token)) {
	        	
	            return RootEntity.error("Geçersiz veya eksik JWT token", null);
	        }
		 
		 
		 	// Token'dan kullanıcı ID'sini çıkar
	        Long userId = jwtTokenProvider.getUserIdFromJwt(token);
		 
	        
	        List<DtoUserLike> likes = likeService.getLikesByUserId(userId);
    	    return RootEntity.ok(likes);
	    }

	


	// like id  ile like getirme
	@GetMapping(path = "/like/{likeId}")
	@Override
	public RootEntity<DtoLike> getOneLike(@PathVariable(name ="likeId") Long likeId) {
		
		
		return ok(likeService.getOneLike(likeId));
	}

	
	//içerik id ile beğenileri getir
	 @GetMapping("/{contentId}")
	 @PreAuthorize("hasAnyRole('USER', 'CONTENT_ADMIN', 'SUPER_ADMIN')")
	 @Override
	 public RootEntity<List<DtoLike>> getLikesByContentId(@PathVariable Long contentId,  HttpServletRequest request) {
	    	
	    	// Çerezlerden JWT token'ı al
	        String token = extractJwtFromRequest(request);

	        // Token yoksa veya geçersizse hata dön
	        if (token == null || !jwtTokenProvider.validateToken(token)) {
	        	
	            return RootEntity.error("Geçersiz veya eksik JWT token", null);
	        }
	    	 List<DtoLike> likes = likeService.getLikesByContentId(contentId);
	    	    return RootEntity.ok(likes);
		}
	 
	 //içerik id ile beğenileri getir public
	 @GetMapping("/public/{contentId}")
	 @Override
	 public RootEntity<List<DtoLike>> getLikesByContentId(@PathVariable Long contentId) {
	    	
	    	 List<DtoLike> likes = likeService.getLikesByContentId(contentId);
	    	    return RootEntity.ok(likes);
		}
	    
	    
		
		
 // JWT'yi çerezlerden al
    private String extractJwtFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT".equals(cookie.getName())) {
                    return cookie.getValue();  // JWT değerini döndür
                }
            }
        }
        return null;  // JWT çerezi bulunmazsa null döndür
    }



    
    
    // Beğeni sayısı puvlic
    @PreAuthorize("hasAnyRole('USER','CONTENTADMIN','SUPERADMIN')")
    @Override
    @GetMapping("{contentId}/like-count")
    public RootEntity<Object> getLikeCountByContentId(@PathVariable Long contentId, HttpServletRequest request) {
        // Çerezlerden JWT token'ı al
        String token = extractJwtFromRequest(request);

        // Token yoksa veya geçersizse hata dön
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return RootEntity.error("Geçersiz veya eksik JWT token", null);
        }

        // Yorum sayısını döndür
        int likeCount = likeService.getLikeCountByContentId(contentId);
        return RootEntity.ok(likeCount);
    }
    
    
 // Beğeni sayısı
    @PreAuthorize("hasAnyRole('USER','CONTENTADMIN','SUPERADMIN')")
    @Override
    @GetMapping("/public/{contentId}/like-count")
    public RootEntity<Object> getLikeCountByContentId(@PathVariable Long contentId) {
     
        // Yorum sayısını döndür
        int likeCount = likeService.getLikeCountByContentId(contentId);
        return RootEntity.ok(likeCount);
    }
    
    
    
 // Like oluşturma
    @PutMapping("/{contentId}")
    @Override
    @PreAuthorize("hasAnyRole('USER', 'CONTENT_ADMIN', 'SUPER_ADMIN')")
    public RootEntity<DtoLike> toggleLike(@PathVariable Long contentId, HttpServletRequest request) {
        // Çerezlerden JWT token'ı al
       String token = extractJwtFromRequest(request);

        // Token yoksa veya geçersizse hata dön
       if (token == null || !jwtTokenProvider.validateToken(token)) {
            return RootEntity.error("Geçersiz veya eksik JWT token", null);
        }

        // Token'dan kullanıcı kimliğini al
      Long userId = jwtTokenProvider.getUserIdFromJwt(token);

        // Beğeniyi kaydet
       DtoLike savedLike = likeService.toggleLike(contentId, userId);

        return RootEntity.ok(savedLike);
   }
    
    
    // kullanıcı ieçrik beğen durumu
    @Override
    @GetMapping("/{contentId}/status")
    @PreAuthorize("hasAnyRole('USER', 'CONTENT_ADMIN', 'SUPER_ADMIN')")
    public RootEntity<Boolean> checkUserLikeStatus(@PathVariable Long contentId, HttpServletRequest request) {
        // Çerezlerden JWT token'ı al
        String token = extractJwtFromRequest(request);

        // Token yoksa veya geçersizse hata dön
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return RootEntity.error("Geçersiz veya eksik JWT token", false);
        }

        // Token'dan kullanıcı kimliğini al
        Long userId = jwtTokenProvider.getUserIdFromJwt(token);

        // Kullanıcının içeriği beğenip beğenmediğini kontrol et
        boolean isLiked = likeService.isContentLikedByUser(contentId, userId);

        return RootEntity.ok(isLiked);
    }



    
    
	






	
	

}
