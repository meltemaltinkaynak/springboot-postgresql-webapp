package com.web.service.Impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dto.DtoNotification;
import com.web.dto.DtoUser;
import com.web.exception.BaseException;
import com.web.exception.ErrorMessage;
import com.web.exception.MessageType;
import com.web.model.Notification;
import com.web.model.NotificationType;
import com.web.model.User;
import com.web.repository.NotificationRepository;
import com.web.repository.UserRepository;
import com.web.service.INotificationService;

@Service
public class NotificationServiceImpl implements INotificationService{
	
	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private UserRepository userRepository;

	
	// Yeni bildirim oluşturma
	@Override
	public void createNotification(Long userId, String message, NotificationType type) {
		
		Optional<User> optional = userRepository.findById(userId);
		if(optional.isEmpty()) {
			throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, userId.toString()));
		}
		
		User dbUser = optional.get();
		
		Notification notification = new Notification();
		notification.setUser(dbUser);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        
        notificationRepository.save(notification);
	}

	// Bildirimleri getirme

	@Override
	public List<DtoNotification> getNotifications(Long userId) {
		
		List<DtoNotification> response = new ArrayList<>();
		
		List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
		
		for (Notification notification : notifications) {
			DtoNotification dto = new DtoNotification();
			BeanUtils.copyProperties(notification,dto);
			response.add(dto);
		}
		 return response;
	}

	
	// Bildirimi okundu olarak işaretleme
	@Override
	public void markNotificationAsRead(Long notificationId) {
		Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
		
		if (optionalNotification.isEmpty()) {
	        throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, notificationId.toString()));
	    }
		Notification notification = optionalNotification.get();
	    if (!notification.isRead()) {                   //  bildirimokunmadıysa
	        notification.setRead(true);                 // okundu olarak işaretle
	        notificationRepository.save(notification); 
	    }
	}
	

	

	
	
	
	
	

	
	
	
}
