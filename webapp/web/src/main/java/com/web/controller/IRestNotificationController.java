package com.web.controller;

import java.util.List;

import com.web.dto.DtoNotification;
import com.web.dto.RootEntity;

public interface IRestNotificationController {

	// Bildirimleri getirme
    public RootEntity<List<DtoNotification>> getNotifications(Long userId);
    
    
     // Bildirimi okundu olarak işaretleme
    public void markNotificationAsRead(Long notificationId);
}
