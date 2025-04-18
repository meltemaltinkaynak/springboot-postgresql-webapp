package com.web.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.web.exception.MessageType;
import com.web.model.User;
import com.web.repository.UserRepository;
import com.web.security.JwtUserDetails;

import jakarta.websocket.server.ServerEndpoint;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user  = userRepository.findByEmail(email);
		// Eğer kullanıcı bulunmazsa, MessageType enum'ı ile hata mesajı alıyoruz ve fırlatıyoruz
        if (user == null) {
            throw new UsernameNotFoundException(MessageType.USER_NOT_FOUND.getMessage() + ": " + email);
        }
		return JwtUserDetails.create(user);
	}
	
	public UserDetails loadUserById(Long id) {
		User user = userRepository.findById(id).get();
		return JwtUserDetails.create(user);
	}

}
