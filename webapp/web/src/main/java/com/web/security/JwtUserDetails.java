package com.web.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.web.model.User;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class JwtUserDetails implements UserDetails {

	 	private Long id;
	    private String email;
	    private String password;
	    private GrantedAuthority authority;  // Tek bir rol (yetki) alanı

	    private JwtUserDetails(Long id, String email, String password, GrantedAuthority authority) {
	        this.id = id;
	        this.email = email;
	        this.password = password;
	        this.authority = authority;
	    }

	    // User nesnesinden JwtUserDetails oluşturuyoruz
	    public static JwtUserDetails create(User user) {
	        // Kullanıcının rolünü SimpleGrantedAuthority'ye çeviriyoruz
	        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());

	        return new JwtUserDetails(user.getId(), user.getEmail(), user.getPassword(), authority);
	    }

	    // Spring Security'ye kullanıcının yetkilerini döndürüyoruz (tek bir yetki)
	    @Override
	    public Collection<? extends GrantedAuthority> getAuthorities() {
	        return List.of(this.authority);  
	    }

	    @Override
	    public String getUsername() {  // E-posta, kullanıcı adı olarak kullanılacak
	        return this.email;
	    }

	    @Override
	    public boolean isAccountNonExpired() {
	        return true;
	    }

	    @Override
	    public boolean isAccountNonLocked() {
	        return true;
	    }

	    @Override
	    public boolean isCredentialsNonExpired() {
	        return true;
	    }

	    @Override
	    public boolean isEnabled() {
	        return true;
	    }
}