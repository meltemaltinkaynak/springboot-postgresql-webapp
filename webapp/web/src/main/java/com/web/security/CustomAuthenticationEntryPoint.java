package com.web.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.dto.RootEntity;
import com.web.exception.MessageType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
	@Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8"); // Türkçe karakterlerin bozulmaması için
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // JSON formatında hata mesajı oluştur
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("messageType", MessageType.UNAUTHORIZED_ACTION);
        errorData.put("description", "Bu işlemi gerçekleştirebilmek için giriş yapmalısınız. ");

        // RootEntity kullanarak hata mesajını oluştur
        RootEntity<Map<String, Object>> errorResponse = RootEntity.error("Yetkisiz erişim", errorData);

        // JSON olarak yaz
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }

}
