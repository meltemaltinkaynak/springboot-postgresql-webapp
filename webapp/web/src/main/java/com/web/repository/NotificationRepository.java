package com.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.web.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>{

	// Belirtilen userId’ye sahip bildirimleri getirir ve tarihe göre en son ekleneni en üstte gösterir.
	List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
	// SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC;
}
