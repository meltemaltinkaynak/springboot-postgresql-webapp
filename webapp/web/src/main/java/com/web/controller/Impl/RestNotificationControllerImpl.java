package com.web.controller.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.web.controller.IRestNotificationController;
import com.web.dto.DtoNotification;
import com.web.dto.RootEntity;
import com.web.exception.BaseException;
import com.web.service.INotificationService;


@RestController
@RequestMapping("/rest/api/notification")
public class RestNotificationControllerImpl extends RestBaseController implements IRestNotificationController{

	
	@Autowired
	private INotificationService notificationService;

	// Bildirimleri getirme
	 @GetMapping("/{userId}")
	@Override
	public RootEntity<List<DtoNotification>> getNotifications(@PathVariable Long userId) {
		
		return ok(notificationService.getNotifications(userId));
	}

	// Bildirimi okundu olarak işaretleme
	@PatchMapping("/{notificationId}/read")
	@Override
	public void markNotificationAsRead(@PathVariable Long notificationId) {
		notificationService.markNotificationAsRead(notificationId);
		
	}

	
	

	


	
	
	

	
	
	
}
