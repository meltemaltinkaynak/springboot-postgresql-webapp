package com.web.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_session")

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//  @ManyToOne(fetch = FetchType.LAZY) // Content çektiğimizde user objesi gelmesin diye
    @ManyToOne
    @JoinColumn(name = "user_id", nullable=true)
    
    @JsonIgnore
    private User user;  // Kullanıcı ile ilişki

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    @Column(name = "device_name")
    private String deviceName;

    

    @Column(name = "is_active", nullable = false)
    private boolean isActive;
    
    

}
