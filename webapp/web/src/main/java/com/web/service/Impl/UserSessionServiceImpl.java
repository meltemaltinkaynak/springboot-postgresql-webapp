package com.web.service.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.model.User;
import com.web.model.UserSession;
import com.web.repository.UserRepository;
import com.web.repository.UserSessionRepository;
import com.web.service.IContentService;
import com.web.service.IUserSessionService;

@Service
public class UserSessionServiceImpl  implements IUserSessionService{
	
	  	@Autowired
	    private UserSessionRepository userSessionRepository;

	    @Autowired
	    private UserRepository userRepository;
	    

	    
	    
	    @Override
	    public  UserSession createUserSession(User user, String ipAddress, String deviceName) {
	        UserSession userSession = new UserSession();
	        userSession.setUser(user);
	        userSession.setIpAddress(ipAddress);
	        userSession.setDeviceName(deviceName);
	       
	        userSession.setLoginTime(LocalDateTime.now());
	        userSession.setActive(true);  // Oturum aktif olarak açılır

	        userSessionRepository.save(userSession);  // Oturum veritabanına kaydedilir
	        
	        // Kaydedilen oturumun ID'si döndürülür
	        return userSession;
	    }
	    

	    
	    @Override
	    public void terminateUserSession(Long sessionId) {
	        Optional<UserSession> userSessionOpt = userSessionRepository.findById(sessionId);

	        if (userSessionOpt.isPresent()) {
	            UserSession userSession = userSessionOpt.get();
	            userSession.setActive(false);  // Oturumu sonlandır
	            userSessionRepository.save(userSession);
	        }
	    }
	    
	    @Override
	    public void logoutUser(User user) {
	        userSessionRepository.findTopByUserAndIsActiveOrderByLoginTimeDesc(user, true)
	            .ifPresent(session -> {
	                session.setActive(false);
	                userSessionRepository.save(session);
	            });
	    }
	    
	    @Override
	    public List<UserSession> getUserSessions(User user) {
	        return userSessionRepository.findByUserOrderByLoginTimeDesc(user);
	    }
	    
	    
	    // sessionId'nin aktif olup olmadığını kontrol et
	    public boolean isSessionActive(Long sessionId) {
	        UserSession userSession = userSessionRepository.findById(sessionId).orElse(null);

	        // Eğer session bulunamazsa veya session aktif değilse false döner
	        return userSession != null && userSession.isActive();
	    }

}
