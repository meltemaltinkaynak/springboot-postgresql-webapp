package com.web.service;

import java.util.List;

import com.web.dto.DtoNotification;
import com.web.dto.DtoUser;
import com.web.model.NotificationType;

public interface INotificationService {
	
	// Yeni bildirim oluşturma
    public void createNotification(Long userId, String message, NotificationType type);
    
    // Bildirimleri getirme
    public List<DtoNotification> getNotifications(Long userId);
    
    // Bildirimi okundu olarak işaretleme
    public void markNotificationAsRead(Long notificationId);
	
	
}
