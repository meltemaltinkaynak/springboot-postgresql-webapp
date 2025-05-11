package com.web.controller.Impl;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.web.controller.IRestUserController;
import com.web.dto.DeleteAccount;
import com.web.dto.DtoAuthor;
import com.web.dto.DtoChangePassword;
import com.web.dto.DtoContent;
import com.web.dto.DtoUser;
import com.web.dto.DtoUserContent;
import com.web.dto.DtoUserIU;
import com.web.dto.DtoUserProfil;
import com.web.dto.RootEntity;
import com.web.exception.BaseException;
import com.web.exception.ErrorMessage;
import com.web.exception.MessageType;
import com.web.security.JwtTokenProvider;
import com.web.service.IUserService;
import com.web.service.Impl.FileStorageServiceImpl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

//@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/users")
public class RestUserControllerImpl  extends RestBaseController implements IRestUserController{

	@Autowired
	private IUserService userService;
	
	@Autowired
    private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private  FileStorageServiceImpl fileStorageService;



	// User profil
	@PreAuthorize("hasAnyRole('USER','CONTENTADMIN','SUPERADMIN')")
	@GetMapping("/{userId}")
	@Override
	public RootEntity<DtoUserProfil> getUserProfile(@PathVariable(name = "userId") Long id, HttpServletRequest request) {
		
		// Çerezlerden JWT token'ı al
        String token = extractJwtFromRequest(request);

        // Token yoksa veya geçersizse hata dön
        if (token == null || !jwtTokenProvider.validateToken(token)) {
        	
            return RootEntity.error("Geçersiz veya eksik JWT token", null);
        }

		return ok(userService.getUserProfile(id));
	}

	// User list
	@GetMapping
	@Override
	public RootEntity<List<DtoUser>> getUserList() {
		
		return ok(userService.getUserList());
	}



	// User update
	@Override
	@PatchMapping("/{userId}")
	@PreAuthorize("hasAnyRole('USER','CONTENTADMIN','SUPERADMIN')")
	public RootEntity<Map<String, Object>> patchUser(
	        @PathVariable(name = "userId") Long userId,
	        @RequestBody Map<String, Object> updates,  // JSON formatında gelen veriler
	        HttpServletRequest request) {

	    // Çerezlerden JWT token'ı al
	    String token = extractJwtFromRequest(request);

	    // Token yoksa veya geçersizse hata dön
	    if (token == null || !jwtTokenProvider.validateToken(token)) {
	        return RootEntity.error("Geçersiz veya eksik JWT token", null);
	    }

	    // Fotoğraf varsa, Base64 string'ini çöz ve kaydet
	    String profilePhotoBase64 = (String) updates.get("profilePhoto");
	    if (profilePhotoBase64 != null && !profilePhotoBase64.isEmpty()) {
	        String photoUrl = fileStorageService.ppstoreFileFromBase64(profilePhotoBase64); // Base64 çöz ve kaydet
	        updates.put("profilePhoto", photoUrl);  // Fotoğrafın URL'sini güncellemeye ekle
	    }

	    // Kullanıcıyı güncelle
	    return ok(userService.patchUser(userId, updates));
	}
	

	
	// userId ile içerik yazarı
	@GetMapping("/author/{userId}")
	@PreAuthorize("hasAnyRole('USER','CONTENTADMIN','SUPERADMIN')")
	@Override
	public RootEntity<DtoAuthor> getAuthorById(@PathVariable(name = "userId")  Long userId, HttpServletRequest request) {
		
		// Çerezlerden JWT token'ı al
        String token = extractJwtFromRequest(request);

        // Token yoksa veya geçersizse hata dön
        if (token == null || !jwtTokenProvider.validateToken(token)) {
        	
            return RootEntity.error("Geçersiz veya eksik JWT token", null);
        }

        

        DtoAuthor author = userService.getAuthorById(userId);
        
        return RootEntity.ok(author);
    }
	
	// userId ile içerik yazarı
	@GetMapping("/public/author/{userId}")
	@Override
	public RootEntity<DtoAuthor> getAuthorById(@PathVariable(name = "userId")  Long userId) {
		
		     
        DtoAuthor author = userService.getAuthorById(userId);
        
        return RootEntity.ok(author);
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
    
    
    @PostMapping("/change-password")
    @Override
    public RootEntity<DtoUser> changePassword(@RequestBody @Valid DtoChangePassword dtoChangePassword,
    										HttpServletRequest request,  HttpServletResponse response) {

    	String token = extractJwtFromRequest(request);
        
        // Token doğrulaması
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return RootEntity.error("Geçersiz veya eksik JWT token", null);
        }

        Long userId = jwtTokenProvider.getUserIdFromJwt(token);
        
        try {
            // Şifreyi değiştirme işlemi
            DtoUser updatedUser = userService.changePassword(userId, dtoChangePassword);
         // Token'ları geçersiz kıl
            invalidateTokens(request, response);
            return RootEntity.ok(updatedUser);  // Başarı durumu
        } catch (IllegalArgumentException e) {
            // Hata durumu (örneğin geçersiz parametre)
        	return RootEntity.error(e.getMessage(), null);
        
        } catch (Exception e) {
            // Genel hata durumu
        	return RootEntity.error(e.getMessage(), null);
        
        }

    }
    
    
    @PostMapping("/delete-account")
    @Override
    public RootEntity<String> deleteAccount(
            @RequestBody @Valid DeleteAccount deleteAccountRequest,
            HttpServletRequest request,
            HttpServletResponse response) {

        // JWT token'ı al
        String token = extractJwtFromRequest(request);

        // Token doğrulaması
        if (token == null || !jwtTokenProvider.validateToken(token)) {
        	  return RootEntity.error("Geçersiz veya eksik JWT token", null);
        }

        Long userId = jwtTokenProvider.getUserIdFromJwt(token);

        try {
            // Hesap silme işlemi
            userService.deleteUser(userId, deleteAccountRequest.isDeleteContent());

            // Token'ları geçersiz kıl
            invalidateTokens(request, response);

            return RootEntity.ok("Hesap başarıyla silindi");
        } catch (IllegalArgumentException e) {
            // Hata durumu
        	return RootEntity.error(e.getMessage(), null);
        } catch (Exception e) {
            // Genel hata durumu
        	return RootEntity.error(e.getMessage(), null);
        }
    }

    
    public void invalidateTokens(HttpServletRequest request, HttpServletResponse response) {
        // JWT ve Refresh Token'ları geçersiz hale getir
        Cookie jwtCookie = new Cookie("JWT", null);
        jwtCookie.setMaxAge(0);
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setAttribute("SameSite", "None");
        response.addCookie(jwtCookie);

        Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setAttribute("SameSite", "None");
        response.addCookie(refreshTokenCookie);
    }



	
	
}
