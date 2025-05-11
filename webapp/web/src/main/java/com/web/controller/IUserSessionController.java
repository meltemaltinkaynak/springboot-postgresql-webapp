package com.web.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;



import com.web.dto.RootEntity;
import com.web.dto.SessionRequest;
import com.web.model.UserSession;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IUserSessionController {
	
	 public RootEntity<String> createSession(@RequestBody SessionRequest sessionRequest);
	 public RootEntity<String> terminateSession(@PathVariable Long sessionId);
	
	 public RootEntity<List<UserSession>> getUserSessions(HttpServletRequest request);
	 
	 
	 

}
