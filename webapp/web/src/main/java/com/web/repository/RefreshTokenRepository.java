package com.web.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.web.model.RefreshToken;

@Repository
public interface RefreshTokenRepository  extends JpaRepository<RefreshToken, Long>{

	RefreshToken findByUserId(Long userId);
	
	

}
