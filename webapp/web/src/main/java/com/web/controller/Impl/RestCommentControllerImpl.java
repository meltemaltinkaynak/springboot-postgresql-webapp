package com.web.controller.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.controller.IRestCommentController;
import com.web.dto.DtoComment;
import com.web.dto.DtoCommentIU;
import com.web.dto.DtoContentComment;
import com.web.dto.DtoUserComment;
import com.web.dto.DtoUserCommentList;
import com.web.dto.DtoUserLike;
import com.web.dto.RootEntity;
import com.web.security.JwtTokenProvider;
import com.web.service.ICommentService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "https://127.0.0.1:5500", allowCredentials = "true")

@RestController
@RequestMapping("/comments")
public class RestCommentControllerImpl extends RestBaseController implements IRestCommentController{

	@Autowired
	private ICommentService commentService;
	
	@Autowired
    private JwtTokenProvider jwtTokenProvider;

	
	// Tüm yorumları getirme
	@GetMapping
	@Override
	public RootEntity<List<DtoComment>> getAllComments() {
		// TODO Auto-generated method stub
		return ok(commentService.getAllComments());
	}

	// kullanıcı id  ile yorumları getirme
	@Override
	@GetMapping("/comment-list")
	public RootEntity<List<DtoUserCommentList>>getCommentsByUserI(HttpServletRequest request) {
			 
		// Çerezlerden JWT token'ı al
        String token = extractJwtFromRequest(request);

        // Token yoksa veya geçersizse hata dön
        if (token == null || !jwtTokenProvider.validateToken(token)) {
        	
            return RootEntity.error("Geçersiz veya eksik JWT token", null);
        }
	 
	 
	 	// Token'dan kullanıcı ID'sini çıkar
        Long userId = jwtTokenProvider.getUserIdFromJwt(token);
	 
			 
		        
		 List<DtoUserCommentList> comments = commentService.getCommentsByUserId(userId);
	     return RootEntity.ok(comments);
	}

	
	// Yorum id  ile yorum getirme
	@GetMapping(path = "/{commentId}")
	@Override
	public RootEntity<DtoComment> getOneComment(@PathVariable(name ="commentId")  Long commentId) {
		
		return ok(commentService.getOneComment(commentId));
	}

	// Content id ile yorum getirme
	@GetMapping(path = "/content/{contentId}")
	@Override
	public RootEntity<DtoContentComment> getComment(@PathVariable(name ="contentId") Long contentId) {
		
		return ok(commentService.getComment(contentId));
	}

	
	
	
	
	
	// yorum silme
    @DeleteMapping(path = "/{userId}/{commentId}")
    @Override
    public void deleteComment(@PathVariable(name = "userId") Long userId, @PathVariable(name = "commentId") Long commentId) {
       
        commentService.deleteComment(userId, commentId);
    }

    
    
    
    // içerik id ile yorum getirme
    @GetMapping("/list/{contentId}")
    @PreAuthorize("hasAnyRole('USER', 'CONTENT_ADMIN', 'SUPER_ADMIN')")
	@Override
	public RootEntity<List<DtoComment>> getCommentsByContentId(@PathVariable Long contentId,  HttpServletRequest request) {
    	
    	// Çerezlerden JWT token'ı al
        String token = extractJwtFromRequest(request);

        // Token yoksa veya geçersizse hata dön
        if (token == null || !jwtTokenProvider.validateToken(token)) {
        	
            return RootEntity.error("Geçersiz veya eksik JWT token", null);
        }
        
     // JWT token'ından kullanıcı bilgilerini al (ID veya rol gibi)
        Long userId = jwtTokenProvider.getUserIdFromJwt(token);  // Kullanıcı ID'sini al
        String userRole = jwtTokenProvider.getUserRoleFromJwt(token);  // Kullanıcı rolünü al
        
    	 List<DtoComment> comments = commentService.getCommentsByContentId(contentId);
    	    return RootEntity.ok(comments);
	}
    
    
    
    // içerik id ile yorum getirme public
    @GetMapping("/public/list/{contentId}")
	@Override
	public RootEntity<List<DtoComment>> getCommentsByContentId(@PathVariable Long contentId) {
    	
    	
    	 List<DtoComment> comments = commentService.getCommentsByContentId(contentId);
    	    return RootEntity.ok(comments);
	}
    


    
    //Yorum sayısı
    @PreAuthorize("hasAnyRole('USER','CONTENTADMIN','SUPERADMIN')")
    @Override
    @GetMapping("/{contentId}/comment-count")
    public RootEntity<Object> getCommentCountByContentId(@PathVariable Long contentId, HttpServletRequest request) {
        // Çerezlerden JWT token'ı al
        String token = extractJwtFromRequest(request);

        // Token yoksa veya geçersizse hata dön
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return RootEntity.error("Geçersiz veya eksik JWT token", null);
        }

        // Yorum sayısını döndür
        int commentCount = commentService.getCommentCountByContentId(contentId);
        return RootEntity.ok(commentCount);
    }
    
    
    
    //Yorum sayısı public  
    @Override
    @GetMapping("/public/{contentId}/comment-count")
    public RootEntity<Object> getCommentCountByContentId(@PathVariable Long contentId) {

        // Yorum sayısını döndür
        int commentCount = commentService.getCommentCountByContentId(contentId);
        return RootEntity.ok(commentCount);
    }
    
    

	// Yorum oluşturma
 
    @PostMapping("/{contentId}")
    @Override
    public RootEntity<DtoComment> saveComment(@PathVariable Long contentId, 
                                              @RequestBody DtoCommentIU dtoCommentIU,
                                              HttpServletRequest request) {

        // Çerezlerden JWT token'ı al
        String token = extractJwtFromRequest(request);

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return RootEntity.error(
                "İçeriklere yorum yapabilmek ve daha fazlası için lütfen giriş yapın. " +
                "Hesabınız yoksa kayıt olun.",
                null
            );
        }

        Long userId = jwtTokenProvider.getUserIdFromJwt(token);

        if (userId == null) {
            return RootEntity.error("Kullanıcı kimliği bulunamadı", null);
        }

        if (contentId == null) {
            return RootEntity.error("İçerik kimliği geçersiz", null);
        }

        return RootEntity.ok(commentService.saveComment(contentId, userId, dtoCommentIU));
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

	
    
    
}
