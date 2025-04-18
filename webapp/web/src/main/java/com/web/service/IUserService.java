package com.web.service;

import java.util.List;
import java.util.Map;

import com.web.dto.DtoAuthor;
import com.web.dto.DtoContent;
import com.web.dto.DtoUser;
import com.web.dto.DtoUserContent;
import com.web.dto.DtoUserIU;
import com.web.dto.DtoUserProfil;
import com.web.model.User;

public interface IUserService {

	// User kayıt
	public DtoUser saveUser(DtoUserIU dtoUserIU);
//	User saveUser(User newUser);
	
	// User profil
	public DtoUserProfil getUserProfile(Long id);
	
	// User list
	public List<DtoUser> getUserList();
	
	// User Delete
	public void deleteUser(Long id);
	
//	// User update
//	public DtoUser updateUser(Long id, DtoUserIU dtoUserIU);
	
	public Map<String, Object> patchUser(Long userId, Map<String, Object> updates);

	public User getOneUserByEmail(String email);
	
	//userId ile içeriğin yazarı
	public DtoAuthor getAuthorById(Long userId);

	public User getOneUserById(Long id);
	
	

	

	
	
	
}
