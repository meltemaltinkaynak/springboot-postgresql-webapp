package com.web.controller.Impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.web.controller.IRestContentController;

import com.web.dto.DtoContent;
import com.web.dto.DtoContentIU;
import com.web.dto.DtoUserContent;
import com.web.dto.DtoUserStatistics;
import com.web.dto.RootEntity;
import com.web.model.Content;
import com.web.model.ContentCategory;
import com.web.security.JwtTokenProvider;
import com.web.service.IContentService;
import com.web.service.Impl.FileStorageServiceImpl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/contents")
public class RestContentControllerImpl extends RestBaseController implements IRestContentController{

	
	    @Autowired
	    private IContentService contentService;
	    
	    @Autowired
	    private JwtTokenProvider jwtTokenProvider;
	    
	    @Autowired
		private  FileStorageServiceImpl fileStorageService;
	    
	    
	    
	    //istatistik
	    @GetMapping("/statistics")
	    public RootEntity<DtoUserStatistics> getUserStatistics(HttpServletRequest request) {
	    	
	    	 // Çerezlerden JWT token'ı al
	        String token = extractJwtFromRequest(request);

	        // Token yoksa veya geçersizse hata dön
	        if (token == null || !jwtTokenProvider.validateToken(token)) {
	            return RootEntity.error("Geçersiz veya eksik JWT token", null);
	        }

	        // Token'dan kullanıcı ID'sini çıkar
	        Long userId = jwtTokenProvider.getUserIdFromJwt(token);
	    	
	    	DtoUserStatistics statistics  = contentService.getUserStatistics(userId);
	        return RootEntity.ok(statistics);
	    }
	    
	    

		//Tüm içerikleri getirme
	    @GetMapping
	    @Override
		public RootEntity<List<DtoContent>> getAllContents() {
			
			return ok(contentService.getAllContents());
		}
	    
	    
	    // kullanıcı id'si ile içerik getirme,
	    @Override
	    @GetMapping(path = "/userContents")
	    public RootEntity<List<DtoContent>> getContentsByUserId(HttpServletRequest request) {
	        // Çerezlerden JWT token'ı al
	        String token = extractJwtFromRequest(request);

	        // Token yoksa veya geçersizse hata dön
	        if (token == null || !jwtTokenProvider.validateToken(token)) {
	            return RootEntity.error("Geçersiz veya eksik JWT token", null);
	        }

	        // Token'dan kullanıcı ID'sini çıkar
	        Long userId = jwtTokenProvider.getUserIdFromJwt(token);

	     
	        List<DtoContent> contents = contentService.getContentsByUserId(userId);
	        return RootEntity.ok(contents);
	           
	    }
	    
	    
	    
	    // İçerik id'si ile içerik getirme
	    @GetMapping(path = "/{contentId}")
	    @Override
		public RootEntity<DtoContent>  getContentByContentId(@PathVariable Long contentId, HttpServletRequest request){
			// Çerezlerden JWT token'ı al
	        String token = extractJwtFromRequest(request);

	        // Token yoksa veya geçersizse hata dön
	        if (token == null || !jwtTokenProvider.validateToken(token)) {
	            return RootEntity.error("Geçersiz veya eksik JWT token", null);
	        }
			
	        DtoContent content = contentService.getContentByContentId(contentId);
	        return RootEntity.ok(content);
		}
	    
	    @Override
	    @PostMapping("/create")
	    @PreAuthorize("hasAnyRole('CONTENTADMIN', 'SUPERADMIN')")
	    public RootEntity<String> saveContent(
	            @RequestParam Map<String, String> contentData,
	            @RequestParam(required = false) MultipartFile image,
	            @RequestParam(required = false) boolean isRestricted,  // Bu parametreyi al
	            HttpServletRequest request) {
	        
	        String token = extractJwtFromRequest(request);
	        
	        // Token doğrulaması
	        if (token == null || !jwtTokenProvider.validateToken(token)) {
	            return RootEntity.error("Geçersiz veya eksik JWT token", null);
	        }

	        Long userId = jwtTokenProvider.getUserIdFromJwt(token);

	        try {
	            // ContentData'dan gerekli verileri al
	            String title = contentData.get("title");
	            String text = contentData.get("text");
	            ContentCategory category = ContentCategory.valueOf(contentData.get("category"));

	            // Fotoğraf varsa, Base64 string'ini çöz ve kaydet
	            String imageUrl = null;
	            if (image != null && !image.isEmpty()) {
	                try {
	                    imageUrl = fileStorageService.storeFile(image, isRestricted);  // restricted parametresiyle kaydet
	                } catch (Exception e) {
	                    return RootEntity.error("Fotoğraf yüklenirken bir hata oluştu", null);
	                }
	            }

	            // İçeriği kaydet
	            contentService.saveContent(userId, title, text, category, imageUrl, isRestricted);

	            return RootEntity.ok("İçerik başarıyla yüklendi!");
	        } catch (Exception e) {
	            e.printStackTrace();  // Hata mesajını logla
	            return RootEntity.error("İçerik yüklenirken bir hata oluştu", null);
	        }
	    }




	    
	    
	 // İçerik güncelleme
	    @PatchMapping(path = "/{userId}/{contentId}")
	    @Override
	    public RootEntity<Map<String, Object>> patchContent(@PathVariable Long userId, @PathVariable Long contentId, @RequestBody Map<String, Object> updates) {
	        return ok(contentService.patchContent(userId, contentId, updates));
	    }

	    // İçerik silme
	    @DeleteMapping(path = "/{userId}/{contentId}")
	    @Override
	    public void deleteContent(@PathVariable(name = "userId") Long userId, @PathVariable(name = "contentId") Long contentId) {
	       
	        contentService.deleteContent(userId, contentId);
	    }

	    
	    //içerik kısıt
	    @PutMapping("/restrict/{userId}/{contentId}")
		@Override
		public boolean updateContentRestriction(@PathVariable(name="userId") Long userId, boolean isRestricted, @PathVariable(name="contentId")  Long contentId) {
	    	boolean updatedRestriction = contentService.updateContentRestriction(contentId, isRestricted, userId);
			return updatedRestriction;
		}

	    
	    // kategoriye göre içerik getirme
	    @GetMapping("/category/{category}")
		@Override
		public RootEntity<List<DtoContent>> getContentsByCategory(@PathVariable(name="category") ContentCategory category) {
		
			return ok(contentService.getContentsByCategory(category));
		}

		

	  
	    
	    // son bir hafta ieçrikler
	    @GetMapping("/last-week")
	    @PreAuthorize("hasAnyRole('USER','CONTENTADMIN','SUPERADMIN')")
	    @Override
	    public RootEntity<List<DtoContent>> getRecentContents(HttpServletRequest request) {

	        // Çerezlerden JWT token'ı al
	        String token = extractJwtFromRequest(request);

	        // Token yoksa veya geçersizse hata dön
	        if (token == null || !jwtTokenProvider.validateToken(token)) {
	        	
	            return RootEntity.error("Geçersiz veya eksik JWT token", null);
	        }

	        // JWT token'ından kullanıcı bilgilerini al (ID veya rol gibi)
	        Long userId = jwtTokenProvider.getUserIdFromJwt(token);  // Kullanıcı ID'sini al
	        String userRole = jwtTokenProvider.getUserRoleFromJwt(token);  // Kullanıcı rolünü al

	        
	        // Son bir haftanın içeriklerini al
	        List<DtoContent> recentContents = contentService.getRecentContents();

	        // İçerikleri döndür
	        return RootEntity.ok(recentContents);
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
	    
	    
	    
	    // Herkese açık kısıtsız  webtasrarımı içerikleri
	    @Override
	    @GetMapping("/public/webdesign")
	    public RootEntity<List<DtoContent>> getWebDesignPublicContents() {
	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndRestricted(ContentCategory.WEBTASARIMI, false);
	        return RootEntity.ok(contents);
	    }

	    
	    // Giriş yapmış kullanıcıya webtasarımı tüm içerikler
	    @Override
	    @GetMapping("/webdesign")
	    @PreAuthorize("hasAnyRole('USER', 'CONTENTADMIN', 'SUPERADMIN')")
	    public RootEntity<List<DtoContent>> getAllWebDesignContents(HttpServletRequest request) {
	        // Çerezlerden JWT token'ı al
	        String token = extractJwtFromRequest(request);

	        // Token yoksa veya geçersizse hata dön
	        if (token == null || !jwtTokenProvider.validateToken(token)) {
	            return RootEntity.error("Geçersiz veya eksik JWT token", null);
	        }

	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndNoRestriction(ContentCategory.WEBTASARIMI);
	        return RootEntity.ok(contents);
	    }
	    
	 
	    
	    // Herkese açık kısıtsız  html içerikleri
	    @Override
	    @GetMapping("/public/html")
	    public RootEntity<List<DtoContent>> getHtmlPublicContents() {
	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndRestricted(ContentCategory.HTML, false);
	        return RootEntity.ok(contents);
	    }

	    
	    // Giriş yapmış kullanıcıya html tüm içerikler
	    @Override
	    @GetMapping("/html")
	    @PreAuthorize("hasAnyRole('USER', 'CONTENTADMIN', 'SUPERADMIN')")
	    public RootEntity<List<DtoContent>> getAllHtmlContents(HttpServletRequest request) {
	        // Çerezlerden JWT token'ı al
	        String token = extractJwtFromRequest(request);

	        // Token yoksa veya geçersizse hata dön
	        if (token == null || !jwtTokenProvider.validateToken(token)) {
	            return RootEntity.error("Geçersiz veya eksik JWT token", null);
	        }

	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndNoRestriction(ContentCategory.HTML);
	        return RootEntity.ok(contents);
	    }
	    
	    
	 // Herkese açık kısıtsız  CSS içerikleri
	    @Override
	    @GetMapping("/public/css")
	    public RootEntity<List<DtoContent>> getCSSPublicContents() {
	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndRestricted(ContentCategory.CSS, false);
	        return RootEntity.ok(contents);
	    }

	    
	    // Giriş yapmış kullanıcıya CSS tüm içerikler
	    @Override
	    @GetMapping("/css")
	    @PreAuthorize("hasAnyRole('USER', 'CONTENTADMIN', 'SUPERADMIN')")
	    public RootEntity<List<DtoContent>> getAllCSSContents(HttpServletRequest request) {
	        // Çerezlerden JWT token'ı al
	        String token = extractJwtFromRequest(request);

	        // Token yoksa veya geçersizse hata dön
	        if (token == null || !jwtTokenProvider.validateToken(token)) {
	            return RootEntity.error("Geçersiz veya eksik JWT token", null);
	        }

	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndNoRestriction(ContentCategory.CSS);
	        return RootEntity.ok(contents);
	    }
	    
	    
	    // Herkese açık kısıtsız  javascript içerikleri
	    @Override
	    @GetMapping("/public/javascript")
	    public RootEntity<List<DtoContent>> getJavaScriptPublicContents() {
	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndRestricted(ContentCategory.JAVASCRIPT, false);
	        return RootEntity.ok(contents);
	    }

	    
	    // Giriş yapmış kullanıcıya javascript tüm içerikler
	    @Override
	    @GetMapping("/javascript")
	    @PreAuthorize("hasAnyRole('USER', 'CONTENTADMIN', 'SUPERADMIN')")
	    public RootEntity<List<DtoContent>> getAllJavaScriptContents(HttpServletRequest request) {
	        // Çerezlerden JWT token'ı al
	        String token = extractJwtFromRequest(request);

	        // Token yoksa veya geçersizse hata dön
	        if (token == null || !jwtTokenProvider.validateToken(token)) {
	            return RootEntity.error("Geçersiz veya eksik JWT token", null);
	        }

	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndNoRestriction(ContentCategory.JAVASCRIPT);
	        return RootEntity.ok(contents);
	    }
	    
	    
	    // Herkese açık kısıtsız  VERITABANI içerikleri
	    @Override
	    @GetMapping("/public/veritabani")
	    public RootEntity<List<DtoContent>> getVeritabaniPublicContents() {
	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndRestricted(ContentCategory.VERITABANI, false);
	        return RootEntity.ok(contents);
	    }

	    
	    // Giriş yapmış kullanıcıya VERITABANI tüm içerikler
	    @Override
	    @GetMapping("/veritabani")
	    @PreAuthorize("hasAnyRole('USER', 'CONTENTADMIN', 'SUPERADMIN')")
	    public RootEntity<List<DtoContent>> getAllVeritabaniContents(HttpServletRequest request) {
	        // Çerezlerden JWT token'ı al
	        String token = extractJwtFromRequest(request);

	        // Token yoksa veya geçersizse hata dön
	        if (token == null || !jwtTokenProvider.validateToken(token)) {
	            return RootEntity.error("Geçersiz veya eksik JWT token", null);
	        }

	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndNoRestriction(ContentCategory.VERITABANI);
	        return RootEntity.ok(contents);
	    }
	    
	    
	 // Herkese açık kısıtsız  BACKENDTEKNOLOJILERI içerikleri
	    @Override
	    @GetMapping("/public/backendteknolojileri")
	    public RootEntity<List<DtoContent>> getbackendteknolojileriPublicContents() {
	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndRestricted(ContentCategory.BACKENDTEKNOLOJILERI, false);
	        return RootEntity.ok(contents);
	    }

	    
	    // Giriş yapmış kullanıcıya BACKENDTEKNOLOJILERI tüm içerikler
	    @Override
	    @GetMapping("/backendteknolojileri")
	    @PreAuthorize("hasAnyRole('USER', 'CONTENTADMIN', 'SUPERADMIN')")
	    public RootEntity<List<DtoContent>> getAllbackendteknolojileriContents(HttpServletRequest request) {
	        // Çerezlerden JWT token'ı al
	        String token = extractJwtFromRequest(request);

	        // Token yoksa veya geçersizse hata dön
	        if (token == null || !jwtTokenProvider.validateToken(token)) {
	            return RootEntity.error("Geçersiz veya eksik JWT token", null);
	        }

	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndNoRestriction(ContentCategory.BACKENDTEKNOLOJILERI);
	        return RootEntity.ok(contents);
	    }
	    
	    
	 // Herkese açık kısıtsız  APIiçerikleri
	    @Override
	    @GetMapping("/public/api")
	    public RootEntity<List<DtoContent>> getAPIPublicContents() {
	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndRestricted(ContentCategory.API, false);
	        return RootEntity.ok(contents);
	    }

	    
	    // Giriş yapmış kullanıcıya API tüm içerikler
	    @Override
	    @GetMapping("/api")
	    @PreAuthorize("hasAnyRole('USER', 'CONTENTADMIN', 'SUPERADMIN')")
	    public RootEntity<List<DtoContent>> getAllAPIContents(HttpServletRequest request) {
	        // Çerezlerden JWT token'ı al
	        String token = extractJwtFromRequest(request);

	        // Token yoksa veya geçersizse hata dön
	        if (token == null || !jwtTokenProvider.validateToken(token)) {
	            return RootEntity.error("Geçersiz veya eksik JWT token", null);
	        }

	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndNoRestriction(ContentCategory.API);
	        return RootEntity.ok(contents);
	    }
	    
	    
	 // Herkese açık kısıtsız  SUNUCUHOSTING içerikleri
	    @Override
	    @GetMapping("/public/hosting")
	    public RootEntity<List<DtoContent>> getHostingPublicContents() {
	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndRestricted(ContentCategory.SUNUCUHOSTING, false);
	        return RootEntity.ok(contents);
	    }

	    
	    // Giriş yapmış kullanıcıya SUNUCUHOSTING  tüm içerikler
	    @Override
	    @GetMapping("/hosting")
	    @PreAuthorize("hasAnyRole('USER', 'CONTENTADMIN', 'SUPERADMIN')")
	    public RootEntity<List<DtoContent>> getAllHostingContents(HttpServletRequest request) {
	        // Çerezlerden JWT token'ı al
	        String token = extractJwtFromRequest(request);

	        // Token yoksa veya geçersizse hata dön
	        if (token == null || !jwtTokenProvider.validateToken(token)) {
	            return RootEntity.error("Geçersiz veya eksik JWT token", null);
	        }

	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndNoRestriction(ContentCategory.SUNUCUHOSTING);
	        return RootEntity.ok(contents);
	    }
	    
	    
	 // Herkese açık kısıtsız  SUIUXiçerikleri
	    @Override
	    @GetMapping("/public/uiux")
	    public RootEntity<List<DtoContent>> getUIUXPublicContents() {
	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndRestricted(ContentCategory.UIUX, false);
	        return RootEntity.ok(contents);
	    }

	    
	    // Giriş yapmış kullanıcıya UIUX  tüm içerikler
	    @Override
	    @GetMapping("/uiux")
	    @PreAuthorize("hasAnyRole('USER', 'CONTENTADMIN', 'SUPERADMIN')")
	    public RootEntity<List<DtoContent>> getAllUIUXContents(HttpServletRequest request) {
	        // Çerezlerden JWT token'ı al
	        String token = extractJwtFromRequest(request);

	        // Token yoksa veya geçersizse hata dön
	        if (token == null || !jwtTokenProvider.validateToken(token)) {
	            return RootEntity.error("Geçersiz veya eksik JWT token", null);
	        }

	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndNoRestriction(ContentCategory.UIUX);
	        return RootEntity.ok(contents);
	    }
	    
	    
	 // Herkese açık kısıtsız  TASARIMARACLARI içerikleri
	    @Override
	    @GetMapping("/public/tasarimaraclari")
	    public RootEntity<List<DtoContent>> getTasarimAraclariPublicContents() {
	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndRestricted(ContentCategory.TASARIMARACLARI, false);
	        return RootEntity.ok(contents);
	    }

	    
	    // Giriş yapmış kullanıcıya TASARIMARACLARI  tüm içerikler
	    @Override
	    @GetMapping("/tasarimaraclari")
	    @PreAuthorize("hasAnyRole('USER', 'CONTENTADMIN', 'SUPERADMIN')")
	    public RootEntity<List<DtoContent>> getTasarimAraclariContents(HttpServletRequest request) {
	        // Çerezlerden JWT token'ı al
	        String token = extractJwtFromRequest(request);

	        // Token yoksa veya geçersizse hata dön
	        if (token == null || !jwtTokenProvider.validateToken(token)) {
	            return RootEntity.error("Geçersiz veya eksik JWT token", null);
	        }

	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndNoRestriction(ContentCategory.TASARIMARACLARI);
	        return RootEntity.ok(contents);
	    }
	    
	    
	    // Herkese açık kısıtsız  WEBPERFORMANSISEO içerikleri
	    @Override
	    @GetMapping("/public/webperformans")
	    public RootEntity<List<DtoContent>> getWebPerformansSeoPublicContents() {
	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndRestricted(ContentCategory.WEBPERFORMANSISEO, false);
	        return RootEntity.ok(contents);
	    }

	    
	    // Giriş yapmış kullanıcıya  WEBPERFORMANSISEO  tüm içerikler
	    @Override
	    @GetMapping("/webperformans")
	    @PreAuthorize("hasAnyRole('USER', 'CONTENTADMIN', 'SUPERADMIN')")
	    public RootEntity<List<DtoContent>> getWebPerformansSeoContents(HttpServletRequest request) {
	        // Çerezlerden JWT token'ı al
	        String token = extractJwtFromRequest(request);

	        // Token yoksa veya geçersizse hata dön
	        if (token == null || !jwtTokenProvider.validateToken(token)) {
	            return RootEntity.error("Geçersiz veya eksik JWT token", null);
	        }

	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndNoRestriction(ContentCategory.WEBPERFORMANSISEO);
	        return RootEntity.ok(contents);
	    }
	    
	 // kullanıcı id'si ile içerik getirme,
//	    @Override
//	    @GetMapping(path = "/user/{userId}")
//		public RootEntity<DtoUserContent> getUsersContentList(@PathVariable(name ="userId")  Long userId) {
//			
//			return ok(contentService.getUsersContentList(userId));
//		}
	    
	 


	    
	    
	    // Herkese açık kısıtsız  WEBGUVENLIGI içerikleri
	    @Override
	    @GetMapping("/public/webguvenlik")
	    public RootEntity<List<DtoContent>> getWebGuvenlikPublicContents() {
	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndRestricted(ContentCategory.WEBGUVENLIGI, false);
	        return RootEntity.ok(contents);
	    }

	    
	    // Giriş yapmış kullanıcıya  WEBGUVENLIGI tüm içerikler
	    @Override
	    @GetMapping("/webguvenlik")
	    @PreAuthorize("hasAnyRole('USER', 'CONTENTADMIN', 'SUPERADMIN')")
	    public RootEntity<List<DtoContent>> getWebGuvenlikContents(HttpServletRequest request) {
	        // Çerezlerden JWT token'ı al
	        String token = extractJwtFromRequest(request);

	        // Token yoksa veya geçersizse hata dön
	        if (token == null || !jwtTokenProvider.validateToken(token)) {
	            return RootEntity.error("Geçersiz veya eksik JWT token", null);
	        }

	        // Sonuçları al
	        List<DtoContent> contents = contentService.getContentsByCategoryAndNoRestriction(ContentCategory.WEBGUVENLIGI);
	        return RootEntity.ok(contents);
	    }
	    
	    
	    

	

	
}
