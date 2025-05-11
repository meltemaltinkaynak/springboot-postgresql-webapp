package com.web.controller.Impl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.dto.DtoAuth;
import com.web.dto.DtoLogin;
import com.web.dto.DtoUser;
import com.web.dto.DtoUserIU;
import com.web.dto.RootEntity;
import com.web.dto.SessionRequest;
import com.web.model.Role;
import com.web.model.User;
import com.web.model.UserSession;
import com.web.repository.UserRepository;
import com.web.security.JwtTokenProvider;
import com.web.service.IUserService;
import com.web.service.Impl.RefreshTokenService;
import com.web.service.Impl.UserSessionServiceImpl;

import io.jsonwebtoken.lang.Collections;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.Optional;

import org.hibernate.query.sqm.FetchClauseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




@RestController
@RequestMapping("/auth")
public class RestAuthController  extends RestBaseController {
	
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	

	@Autowired
	private RefreshTokenService refreshTokenService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserSessionServiceImpl userSessionService;

	

	

	@PostMapping("/login")
	public RootEntity<DtoAuth> login(@Valid @RequestBody DtoLogin dtoLogin, 
	                                  HttpServletResponse response, 
	                                  HttpServletRequest request) { 
	    // Kullanıcıyı email ile alıyoruz
	    User user = userService.getOneUserByEmail(dtoLogin.getEmail());

	    // Kullanıcı yoksa hata dönüyoruz
	    if (user == null) {
	        return RootEntity.error("Bu e-posta adresine ait kayıtlı bir kullanıcı bulunmamaktadır.", null);
	    }

	    // Şifre yanlışsa hata dönüyoruz
	    if (!passwordEncoder.matches(dtoLogin.getPassword(), user.getPassword())) {
	        return RootEntity.error("Girilen şifre hatalıdır. Lütfen tekrar deneyiniz..", null);
	    }

	    // Eğer kullanıcı devre dışıysa, otomatik olarak aktif hale getir
	    if (!user.isEnabled()) {
	        user.setEnabled(true);
	        user.enableUserContent();  // İçerikleri, beğenileri, yorumları yeniden aktif hale getir
	        userRepository.save(user);
	    }

	    // Authentication işlemi
	    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dtoLogin.getEmail(), dtoLogin.getPassword());
	    Authentication auth = authenticationManager.authenticate(authToken);
	    SecurityContextHolder.getContext().setAuthentication(auth);

	    // sessionId oluşturuluyor
	    UserSession session = userSessionService.createUserSession(user, request.getRemoteAddr(), request.getHeader("User-Agent"));
	    Long sessionId = session.getId();

	 

	    // Yeni JWT oluşturuluyor
	    String jwtToken = jwtTokenProvider.generateJwtTokenByUserId(user.getId(), sessionId);

	    // Refresh token'ı oluşturuyoruz
	    String refreshToken = refreshTokenService.createRefreshToken(user);

	    // Access token'ı HTTP Only cookie'ye ekliyoruz
	    Cookie accessTokenCookie = new Cookie("JWT", jwtToken);
	    accessTokenCookie.setHttpOnly(true);
	    accessTokenCookie.setSecure(true);
	    accessTokenCookie.setPath("/");
	    accessTokenCookie.setMaxAge(24 * 60 * 60); // 1 gün
	    accessTokenCookie.setAttribute("SameSite", "None");

	    // Refresh token'ı HTTP Only cookie'ye ekliyoruz
	    Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", refreshToken);
	    refreshTokenCookie.setHttpOnly(true);
	    refreshTokenCookie.setSecure(true);
	    refreshTokenCookie.setPath("/");
	    refreshTokenCookie.setMaxAge(30 * 24 * 60 * 60); // 30 gün     
	    refreshTokenCookie.setAttribute("SameSite", "None");

	    // Cookie'leri response'a ekliyoruz
	    response.addCookie(accessTokenCookie);
	    response.addCookie(refreshTokenCookie);

	    // DtoAuth nesnesini dolduruyoruz, sadece kullanıcı bilgilerini gönderiyoruz
	    DtoAuth dtoAuth = new DtoAuth();
	    dtoAuth.setFirstName(user.getFirstName());
	    dtoAuth.setLastName(user.getLastName());

	    // Kullanıcı rolüne göre yönlendirme yapılması
	    String redirectUrl = switch (user.getRole().name()) {
	        case "USER" -> "/user.html"; // User sayfasına yönlendirme
	        case "CONTENTADMIN" -> "/contentadmin.html"; // Content admin sayfasına yönlendirme
	        case "SUPERADMIN" -> "/superadmin.html"; // Superadmin sayfasına yönlendirme
	        default -> "/index.html"; // Varsayılan yönlendirme
	    };
	    dtoAuth.setRedirectUrl(redirectUrl);

	    System.out.println("JWT içinde sessionId: " + sessionId);
	    System.out.println("JWT: " + jwtToken);
	    // Başarılı dönüş
	    return RootEntity.ok(dtoAuth);
	}



	
	@PostMapping("/refresh-token")
	public RootEntity<DtoAuth> refreshToken(HttpServletRequest request) {
	    String refreshToken = null;
	    Cookie[] cookies = request.getCookies();

	    // Refresh token'ı cookies'den al
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if ("REFRESH_TOKEN".equals(cookie.getName())) {
	                refreshToken = cookie.getValue();
	                break;
	            }
	        }
	    }

	    // Refresh token geçerli mi?
	    if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
	        Long userId = jwtTokenProvider.getUserIdFromJwt(refreshToken);
	        Long sessionId = jwtTokenProvider.getSessionIdFromJwt(refreshToken);

	        // Oturum aktif mi?
	        if (!userSessionService.isSessionActive(sessionId)) {
	            return RootEntity.error("Oturum süresi dolmuş veya geçersiz.", null);
	        }

	        // Yeni access token oluştur (aynı sessionId ile)
	        String newAccessToken = jwtTokenProvider.generateJwtTokenByUserId(userId, sessionId);

	        DtoAuth dtoAuth = new DtoAuth();
	       
	        

	        return RootEntity.ok(dtoAuth);
	    } else {
	        return RootEntity.error("Refresh token geçersiz veya süresi dolmuş.", null);
	    }
	}

	// User kayıt
	@PostMapping("/register")
	
	public RootEntity<DtoUser> register(@RequestBody @Valid DtoUserIU dtoUserIU) {
			
		try {
			  DtoUser savedUser = userService.saveUser(dtoUserIU);
		      return ok(savedUser);  // RootEntity.ok() metodunu çağırıyoruz
		   } catch (IllegalArgumentException e) {
		       return Error(e.getMessage()); // Eğer hata varsa RootEntity.error() döndür
		   } catch (Exception e) {
		       return Error("Bilinmeyen bir hata oluştu!"); // Genel hata yakalama
		   }
	}
	
	
	
	@GetMapping("/me")
	public RootEntity<DtoAuth> getCurrentUser(HttpServletRequest request) {
	    String token = null;
	    Cookie[] cookies = request.getCookies();

	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if ("JWT".equals(cookie.getName())) {
	                token = cookie.getValue();
	                break;
	            }
	        }
	    }

	    if (token != null && jwtTokenProvider.validateToken(token)) {
	        Long userId = jwtTokenProvider.getUserIdFromJwt(token);
	        User user = userService.getOneUserById(userId);

	        if (user != null) {
	            DtoAuth dtoAuth = new DtoAuth();
	            dtoAuth.setUserId(user.getId());
	            dtoAuth.setFirstName(user.getFirstName());
	    	    dtoAuth.setLastName(user.getLastName());
	    	    dtoAuth.setRole(user.getRole());
	            

	            return RootEntity.ok(dtoAuth);
	        }
	    }

	    return RootEntity.error("Unauthorized", null);
	}
	
	// Çıkış
	// Çıkış
	@PostMapping("/logout")
	public RootEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
	    Cookie[] cookies = request.getCookies();
	    String jwtToken = null;
	    String refreshToken = null;

	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if ("JWT".equals(cookie.getName())) {
	                jwtToken = cookie.getValue();
	            }
	            if ("REFRESH_TOKEN".equals(cookie.getName())) {
	                refreshToken = cookie.getValue();
	            }
	        }
	    }

	    if (jwtToken != null && jwtTokenProvider.validateToken(jwtToken)) {
	        Long userId = jwtTokenProvider.getUserIdFromJwt(jwtToken);
	        Optional<User> userOpt = userRepository.findById(userId);
	        if (userOpt.isPresent()) {
	            Long sessionId = jwtTokenProvider.getSessionIdFromJwt(jwtToken); 
	            userSessionService.terminateUserSession(sessionId); 
	        }
	    }
	    // Çerezleri temizle
	    Cookie jwtCookie = new Cookie("JWT", null);
	    jwtCookie.setHttpOnly(true);
	    jwtCookie.setSecure(true);
	    jwtCookie.setPath("/");
	    jwtCookie.setMaxAge(0);
	    jwtCookie.setAttribute("SameSite", "None");
	    response.addCookie(jwtCookie);

	    Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", null);
	    refreshTokenCookie.setHttpOnly(true);
	    refreshTokenCookie.setSecure(true);
	    refreshTokenCookie.setPath("/");
	    refreshTokenCookie.setMaxAge(0);
	    refreshTokenCookie.setAttribute("SameSite", "None");
	    response.addCookie(refreshTokenCookie);

	    return RootEntity.ok("Çıkış işlemi başarılı.");
	}


	
	
	@PutMapping("/disable")
	public RootEntity<String> disableUser(HttpServletRequest request, HttpServletResponse response) {
	    // JWT token'ı çerezden al
	    String token = extractJwtFromRequest(request);

	    // Token yoksa veya geçersizse hata dön
	    if (token == null || !jwtTokenProvider.validateToken(token)) {
	        return RootEntity.error("Geçersiz veya eksik JWT token", null);
	    }

	    // Token'dan kullanıcı ID'sini al
	    Long userId = jwtTokenProvider.getUserIdFromJwt(token);

	    // Kullanıcıyı veritabanından al
	    Optional<User> userOpt = userRepository.findById(userId);

	    // Kullanıcı bulunamadıysa hata döndür
	    if (userOpt.isEmpty()) {
	        return RootEntity.error("Kullanıcı bulunamadı.", null);
	    }

	    User user = userOpt.get();

	    // Kullanıcı zaten devre dışıysa hata döndür
	    if (!user.isEnabled()) {
	        return RootEntity.error("Kullanıcı zaten devre dışı.", null);
	    }

	    // Kullanıcıyı devre dışı bırak
	    user.setEnabled(false);
	    user.disableUserContent();  // İçerikleri, beğenileri, yorumları devre dışı bırak
	    userRepository.save(user);

	    // Oturumu sonlandırmak için JWT ve Refresh token'ları geçersiz kıl
	    Cookie accessTokenCookie = new Cookie("JWT", null);
	    accessTokenCookie.setHttpOnly(true);
	    accessTokenCookie.setSecure(true);
	    accessTokenCookie.setPath("/");
	    accessTokenCookie.setMaxAge(0); // Çerezi sil
	    accessTokenCookie.setAttribute("SameSite", "None");

	    Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", null);
	    refreshTokenCookie.setHttpOnly(true);
	    refreshTokenCookie.setSecure(true);
	    refreshTokenCookie.setPath("/");
	    refreshTokenCookie.setMaxAge(0); // Çerezi sil
	    refreshTokenCookie.setAttribute("SameSite", "None");

	    response.addCookie(accessTokenCookie);
	    response.addCookie(refreshTokenCookie);

	    // Başarılı dönüş
	    return RootEntity.ok("Hesabınız devre dışı bırakıldı ve oturumunuz sonlandırılıyor.");
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
