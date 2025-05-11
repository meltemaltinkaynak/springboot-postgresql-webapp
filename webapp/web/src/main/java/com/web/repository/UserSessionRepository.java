package com.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.web.model.User;
import com.web.model.UserSession;

@Repository
public interface UserSessionRepository  extends JpaRepository<UserSession, Long> {
	
	  List<UserSession> findByUser(User user);
	  
	  //Son aktif
	  Optional<UserSession> findTopByUserAndIsActiveOrderByLoginTimeDesc(User user, boolean isActive);
	  
	  List<UserSession> findByUserOrderByLoginTimeDesc(User user);

	

}
