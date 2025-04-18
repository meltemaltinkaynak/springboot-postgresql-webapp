package com.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import com.web.dto.DtoAuthor;
import com.web.dto.DtoUser;
import com.web.dto.DtoUserContent;
import com.web.dto.DtoUserIU;
import com.web.dto.DtoUserProfil;
import com.web.dto.RootEntity;

import jakarta.servlet.http.HttpServletRequest;

public interface IRestUserController {

	// User kayıt
//	public RootEntity<DtoUser> saveUser(DtoUserIU dtoUserIU);
	
	// User profil
	public RootEntity<DtoUserProfil> getUserProfile(Long id,  HttpServletRequest request);
	
	// User list
	public RootEntity<List<DtoUser>> getUserList();
	
	// User Delete
	public void deleteUser(Long id);
	
//	// User update
//	public RootEntity<DtoUser> updateUser(Long id, DtoUserIU dtoUserIU);
	
	public RootEntity<Map<String, Object>> patchUser(Long userId, Map<String, Object> updates, HttpServletRequest request);
	

	//userId ile içeriğin yazarı
	public RootEntity<DtoAuthor> getAuthorById(@PathVariable Long userId, HttpServletRequest request);
	//userId ile içeriğin yazarı public
	public RootEntity<DtoAuthor> getAuthorById(@PathVariable(name = "userId")  Long userId);
}

