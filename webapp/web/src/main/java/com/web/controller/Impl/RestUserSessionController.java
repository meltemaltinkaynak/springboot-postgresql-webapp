package com.web.controller.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.controller.IRestContentController;
import com.web.controller.IUserSessionController;
import com.web.dto.RootEntity;
import com.web.dto.SessionRequest;
import com.web.model.User;
import com.web.model.UserSession;
import com.web.repository.UserRepository;
import com.web.repository.UserSessionRepository;
import com.web.security.JwtTokenProvider;
import com.web.service.IUserService;
import com.web.service.IUserSessionService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/sessions")
public class RestUserSessionController  extends RestBaseController implements IUserSessionController {

	
	  	@Autowired
	    private IUserSessionService userSessionService;
	  	
	  	@Autowired
		private UserRepository userRepository;
	  	
	  	@Autowired
	    private UserSessionRepository userSessionRepository;

	  	

		@Autowired
		private JwtTokenProvider jwtTokenProvider;
		
		

	    // Oturum açıldığında, oturum bilgilerini kaydediyoruz
	  	@Override
	    @PostMapping("/create")
	    public RootEntity<String> createSession(@RequestBody SessionRequest sessionRequest) {
	        // Burada IP, cihaz adı ve konum gibi bilgileri frontend'den alabilirsiniz.
	        User user = userRepository.findById(sessionRequest.getUserId())
	                                  .orElseThrow(() -> new RuntimeException("User not found"));

	        userSessionService.createUserSession(user, sessionRequest.getIpAddress(), 
	                                             sessionRequest.getDeviceName());
	        return RootEntity.ok("Session created successfully");
	    }

	    // Oturum sonlandırma
	  	@Override
	    @PostMapping("/terminate/{sessionId}")
	  	public RootEntity<String> terminateSession(@PathVariable Long sessionId) {
	  		userSessionService.terminateUserSession(sessionId);
	        return RootEntity.ok("Session terminated successfully");
	    }
	  	
	
	  	
	  	@GetMapping("/user")
	  	public RootEntity<List<UserSession>> getUserSessions(HttpServletRequest request) {
	  	    // JWT token'ı çerezlerden al
	  	    String token = extractJwtFromRequest(request);

	  	    // Token yoksa veya geçersizse
	  	    if (token == null || !jwtTokenProvider.validateToken(token)) {
	  	        return RootEntity.error("Geçersiz veya eksik JWT token", null);
	  	    }

	  	    // Token'dan kullanıcı ID'si al
	  	    Long userId = jwtTokenProvider.getUserIdFromJwt(token);

	  	    // Kullanıcıyı veritabanından getir
	  	    User user = userRepository.findById(userId)
	  	                              .orElseThrow(() -> new RuntimeException("User not found"));

	  	    // Oturumları getir
	  	    List<UserSession> sessions = userSessionService.getUserSessions(user);

	  	    return RootEntity.ok(sessions);
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
