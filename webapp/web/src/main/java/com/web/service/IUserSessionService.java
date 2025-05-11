package com.web.service;

import java.util.List;

import com.web.model.User;
import com.web.model.UserSession;

public interface IUserSessionService {

	public  UserSession createUserSession(User user, String ipAddress, String deviceName);
	 
	 public void terminateUserSession(Long sessionId);
	 
	 void logoutUser(User user);
	 
	 List<UserSession> getUserSessions(User user);
	 
}
