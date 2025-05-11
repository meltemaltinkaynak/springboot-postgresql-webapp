package com.web.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.web.service.Impl.UserDetailsServiceImpl;
import com.web.service.Impl.UserSessionServiceImpl;

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
	
	@Autowired
	UserSessionServiceImpl userSessionService;
	
	private static final List<String> EXCLUDED_PATHS = List.of(
			"/auth/login",
			"/auth/me",
			"/auth/register",
			"/auth/logout",
			"/auth/refresh-token",
			
			"/api/sessions/**",
			
			"/api/statistics",
			
			"/comments/public/**",
			
			"/contents/top10",
			"/contents/top10/likes",
			"/contents/top10/comments",
			"/contents/top10/views",
			"/contents/last-week/public",
			"/contents/recent-four",
			
			"/contents/mostViewedContent",
			"/contents/public/**",
			"/contents/guest/**",
			
			"/like/public/**",
			
			"/users/public/**",
			
			"uploads/public/**"		    
		   
		);
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException {
		
		String path = request.getRequestURI();

	    if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
	        filterChain.doFilter(request, response);
	        return;
	    }
	    
	    try {
	        String jwtToken = extractJwtFromRequest(request); // JWT'yi cookie'den alıyoruz
	        if (StringUtils.hasText(jwtToken) && jwtTokenProvider.validateToken(jwtToken)) {
	            Long id = jwtTokenProvider.getUserIdFromJwt(jwtToken);
	            Long sessionId = jwtTokenProvider.getSessionIdFromJwt(jwtToken); // sessionId'yi Long olarak alıyoruz
	            
	            // Session'ın aktif olup olmadığını kontrol ediyoruz
	            if (userSessionService.isSessionActive(sessionId)) {
	                UserDetails user = userDetailsService.loadUserById(id);
	                if (user != null) {
	                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
	                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	                    SecurityContextHolder.getContext().setAuthentication(auth);
	                }
	            } else {
	                // Eğer session aktif değilse, JWT geçersiz kabul edilir
	                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session is no longer active.");
	                return;
	            }
	        }
	    } catch (Exception e) {
	        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token.");
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
