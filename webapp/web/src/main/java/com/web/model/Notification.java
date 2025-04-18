package com.web.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Notification {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "message", nullable = false)
    private String message;  // Bildirim mesajı
	
	
	@Column(name = "is_read", nullable = false)
    private boolean isRead = false;  // Bildirim okunmuş mu?
	
	
	@Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
	
	@Enumerated(EnumType.STRING)
    private NotificationType type;
	
	@PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
	}
	
	@ManyToOne
	private User user;  // Bildirim alan kullanıcı
	
	
}
