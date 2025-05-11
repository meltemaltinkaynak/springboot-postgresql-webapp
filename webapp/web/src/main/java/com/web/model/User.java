package com.web.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "first_name", nullable = false, length = 30)
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 30)
    private String lastName;
    
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(name = "password", nullable = false,length = 255)
    private String password;
    
    @Column(name = "profile_photo")
    private String profilePhoto; 
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role; 
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    @Column(name = "enabled")
    private boolean enabled = true;  // Varsayılan olarak true
    
    // Kontrol metotları
    public void disableUserContent() {
        if (content != null) {
            for (Content c : content) {
                c.setEnabled(false);
            }
        }
        if (comment != null) {
            for (Comment c : comment) {
                c.setEnabled(false);
            }
        }
        if (like != null) {
            for (Like l : like) {
                l.setEnabled(false);
            }
        }
    }

    public void enableUserContent() {
        if (content != null) {
            for (Content c : content) {
                c.setEnabled(true);
            }
        }
        if (comment != null) {
            for (Comment c : comment) {
                c.setEnabled(true);
            }
        }
        if (like != null) {
            for (Like l : like) {
                l.setEnabled(true);
            }
        }
    }
    
    
    // Content ile ilişki
    @OneToMany(mappedBy = "user") // cascade = CascadeType.ALL → Eğer bir kullanıcı silinirse, tüm içerikleri de silinir.
    																			   // orphanRemoval = true → Kullanıcı listesinden içerik çıkarıldığında, içerik otomatik olarak silinir.				
 
	private List<Content> content;  
    
    // Comment ile ilişki
    @OneToMany(mappedBy = "user") // cascade = CascadeType.ALL → Eğer bir kullanıcı silinirse, tüm içerikleri de silinir.
    																			   // orphanRemoval = true → Kullanıcı listesinden içerik çıkarıldığında, içerik otomatik olarak silinir.				
 
	private List<Comment> comment;  
    
    // Like ile ilişki
    @OneToMany(mappedBy = "user") // cascade = CascadeType.ALL → Eğer bir kullanıcı silinirse, tüm içerikleri de silinir.
    																			   // orphanRemoval = true → Kullanıcı listesinden içerik çıkarıldığında, içerik otomatik olarak silinir.				
 
	private List<Like> like;  
    
   // oturum ile ilişki
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSession> userSessions;

}
