package com.web.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.web.model.Role;
import com.web.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	User findByEmail(String email);
	
	
	// web statistics
	@Query("SELECT COUNT(u) FROM User u WHERE u.role = :role1 OR u.role = :role2")
	Long countByRoleIn(Role role1, Role role2);


}
