package com.web.controller.Impl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.dto.DtoAuth;
import com.web.dto.DtoLogin;
import com.web.dto.DtoUser;
import com.web.dto.DtoUserIU;
import com.web.dto.RootEntity;
import com.web.model.Role;
import com.web.model.User;
import com.web.security.JwtTokenProvider;
import com.web.service.IUserService;
import com.web.service.Impl.RefreshTokenService;

import io.jsonwebtoken.lang.Collections;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

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
import org.springframework.web.bind.annotation.PostMapping;
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
	

	

	@PostMapping("/login")
	public RootEntity<DtoAuth> login(@Valid @RequestBody DtoLogin dtoLogin, HttpServletResponse response) {
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

	    // Authentication işlemi
	    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dtoLogin.getEmail(), dtoLogin.getPassword());
	    Authentication auth = authenticationManager.authenticate(authToken);
	    SecurityContextHolder.getContext().setAuthentication(auth);

	    // JWT Token oluşturuluyor
	    String jwtToken = jwtTokenProvider.generateJwtToken(auth);
	    String refreshToken = refreshTokenService.createRefreshToken(user);

	    // Access token'ı HTTP Only cookie'ye ekliyoruz
	    Cookie accessTokenCookie = new Cookie("JWT", jwtToken);
	    accessTokenCookie.setHttpOnly(true);
	    accessTokenCookie.setSecure(true);
	    accessTokenCookie.setPath("/");
	    accessTokenCookie.setMaxAge(24 * 60 * 60); // 1 gün
	    accessTokenCookie.setAttribute("SameSite", "None"); // Ekledik

	    // Refresh token'ı HTTP Only cookie'ye ekliyoruz
	    Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", refreshToken);
	    refreshTokenCookie.setHttpOnly(true);
	    refreshTokenCookie.setSecure(true);
	    refreshTokenCookie.setPath("/");
	    refreshTokenCookie.setMaxAge(30 * 24 * 60 * 60); // 30 gün	    
	    refreshTokenCookie.setAttribute("SameSite", "None"); // Lax veya Strict

	    // Cookie'leri response'a ekliyoruz
	    response.addCookie(accessTokenCookie);
	    response.addCookie(refreshTokenCookie);
	    
	    // DtoAuth nesnesini dolduruyoruz, sadece kullanıcı bilgilerini gönderiyoruz
	    DtoAuth dtoAuth = new DtoAuth();
	    dtoAuth.setFirstName(user.getFirstName());
	    dtoAuth.setLastName(user.getLastName());


	    
	    String redirectUrl;
	    if (user.getRole().name().equals("USER")) {
	        redirectUrl = "/user.html"; // User sayfasına yönlendirme
	    } else if (user.getRole().name().equals("CONTENTADMIN")) {
	        redirectUrl = "/contentadmin.html"; // Content admin sayfasına yönlendirme
	    } else if (user.getRole().name().equals("SUPERADMIN")) {
	        redirectUrl = "/superadmin.html"; // Superadmin sayfasına yönlendirme
	    } else {
	        redirectUrl = "/index.html"; // Varsayılan yönlendirme
	    }
	    dtoAuth.setRedirectUrl(redirectUrl);
	    
	    // Başarılı dönüş
	    return RootEntity.ok(dtoAuth);
	}
	
	@PostMapping("/refresh-token")
	public RootEntity<DtoAuth> refreshToken(HttpServletRequest request) {
	    String refreshToken = null;
	    Cookie[] cookies = request.getCookies();

	    // Refresh token'ı cookies'den alıyoruz
	    for (Cookie cookie : cookies) {
	        if (cookie.getName().equals("REFRESH_TOKEN")) {
	            refreshToken = cookie.getValue();
	            break;
	        }
	    }

	    // Refresh token geçerli mi?
	    if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
	        // Refresh token'dan kullanıcı ID'sini alıyoruz
	        Long userId = jwtTokenProvider.getUserIdFromJwt(refreshToken);

	        // Yeni access token oluşturuluyor
	        String newAccessToken = jwtTokenProvider.generateJwtTokenByUserId(userId);

	        // Yeni access token ile dönülüyor
	        DtoAuth dtoAuth = new DtoAuth();
	        return RootEntity.ok(dtoAuth);  // Yeni access token döndürülüyor
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
	            

	            return RootEntity.ok(dtoAuth);
	        }
	    }

	    return RootEntity.error("Unauthorized", null);
	}



	
	

}
