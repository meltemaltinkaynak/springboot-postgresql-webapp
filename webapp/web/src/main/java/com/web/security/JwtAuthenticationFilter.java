package com.web.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.web.service.Impl.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	
	@Autowired
	JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	UserDetailsServiceImpl userDetailsService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// jwt filter kontrol aşaması, request authorize olmuş mu diye bir filtre
//		try {
//			String jwtToken = extractJwtFromRequest(request);  // gelen HTTP isteğinden JWT'yi alır
//			if(StringUtils.hasText(jwtToken) && jwtTokenProvider.validateToken(jwtToken)) {  // token geçerliyse
//				Long id = jwtTokenProvider.getUserIdFromJwt(jwtToken);  // token'dan kullanıcıya ait id alınır
//				
//				UserDetails user = userDetailsService.loadUserById(id);  // JWT'den alınan kullanıcı id'siyle UserDetailsService üzerinden kullanıcının tüm bilgileri yüklenir.
//				if(user != null) {
//					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()); // UsernamePasswordAuthenticationToken oluşturulur ve kullanıcı bilgileri ile yetkiler (authorities) eklenir.
//					auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));  // WebAuthenticationDetailsSource kullanılarak istekle ilgili ek detaylar eklenir (örneğin IP adresi, oturum bilgileri).
//					SecurityContextHolder.getContext().setAuthentication(auth);  // kullanıcının kimlik doğrulama bilgilerini SecurityContext'e yerleştirir
//				}
//			}
//		} catch(Exception e) {
//			return;
//		}
//		filterChain.doFilter(request, response);  //Spring Security'deki filter chain'ine bir sonraki filtreyi çağırarak devam etmesini sağlar.
//		
//	}
//	
//	// HTTP isteğinden JWT token'ını çıkartma
//	private String extractJwtFromRequest(HttpServletRequest request) {
//		String bearer = request.getHeader("Authorization");
//		if(StringUtils.hasText(bearer) && bearer.startsWith("Bearer "))  //Authorization başlığının varlığını kontrol eder ve  başlığın "Bearer " ile başlayıp başlamadığını kontrol eder.
//			return bearer.substring("Bearer".length() + 1);
//		return null;
//	}
		
		  try {
	            String jwtToken = extractJwtFromRequest(request);  // JWT'yi cookie'den alıyoruz
	            if (StringUtils.hasText(jwtToken) && jwtTokenProvider.validateToken(jwtToken)) {
	                Long id = jwtTokenProvider.getUserIdFromJwt(jwtToken);
	                UserDetails user = userDetailsService.loadUserById(id);
	                if (user != null) {
	                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
	                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	                    SecurityContextHolder.getContext().setAuthentication(auth);
	                }
	            }
	        } catch (Exception e) {
	            return;
	        }
	        filterChain.doFilter(request, response);
	    }
	    
	    // Cookie'den JWT'yi almak için metodu güncelledik
	    private String extractJwtFromRequest(HttpServletRequest request) {
	        Cookie[] cookies = request.getCookies();
	        
	        if (cookies != null) {
	            for (Cookie cookie : cookies) {
	                if ("JWT".equals(cookie.getName())) {
	                    return cookie.getValue();  // JWT'yi cookie'den alıyoruz
	                }
	            }
	        }
	        return null;
	    }
	
		


}
