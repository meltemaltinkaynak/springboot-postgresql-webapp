package com.web.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "admin_assignment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class AdminAssignment {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	
	// Rol değişimini yapan Super Admin
    @ManyToOne
    private User assignedByUser;  // Super Admin'in ID'si
	
    

    @ManyToOne    
    private User assignedUser;  // Rolü değiştirilen kullanıcı
    
    
    private Role newRole;  // Atanan yeni rol

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @PrePersist
    protected void onCreate() {
        this.assignedAt = LocalDateTime.now();
    }
}
