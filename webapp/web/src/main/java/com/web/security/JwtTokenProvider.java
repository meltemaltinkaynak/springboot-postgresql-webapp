package com.web.security;

import java.util.Date;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    @Value("${webapp.app.secret}")
    private String APP_SECRET; 

    @Value("${webapp.expires.in}")
    private long EXPIRES_IN;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(APP_SECRET.getBytes());
    }

    // Kullanıcıdan JWT Token oluşturma
    public String generateJwtTokenByUserId(Long userId, Long sessionId) {
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRES_IN * 1000);

        return Jwts.builder()
            .setSubject(Long.toString(userId))
            .claim("sessionId", sessionId) // sessionId'yi Long olarak ekle
            .setIssuedAt(new Date())
            .setExpiration(expireDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }
   

    // Kullanıcı ID üzerinden JWT Token oluşturma
    public String generateJwtTokenByUserId(Long userId) {
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRES_IN * 1000);

        return Jwts.builder()
            .setSubject(Long.toString(userId))
            .setIssuedAt(new Date())
            .setExpiration(expireDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }

    // Token'dan kullanıcı ID'sini çekme
    public Long getUserIdFromJwt(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        return Long.parseLong(claims.getSubject());
    }
    
 // Token'dan kullanıcı rolünü çekme
    public String getUserRoleFromJwt(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        return claims.get("role", String.class);
    }
    
    public Long getSessionIdFromJwt(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        return claims.get("sessionId", Long.class); // sessionId'yi Long olarak al
    }
    
    
    
 
    // Token geçerliliğini doğrulama
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    // Token süresinin dolup dolmadığını kontrol
    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getExpiration();
        return expiration.before(new Date());
    }
    

}

