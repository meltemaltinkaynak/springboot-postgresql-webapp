package com.web.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Comment {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	

	@Column(name = "text", nullable = false,columnDefinition = "TEXT") //TEXT veri türü,  PostgreSQL'de uzun metinleri saklamak için
    private String text;
	
	@Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
	
	
	@PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
	
	@Column(name = "enabled")
    private boolean enabled = true; // Varsayılan olarak true
	
//	@ManyToOne(fetch = FetchType.LAZY) // Content çektiğimizde user objesi gelmesin diye
    @ManyToOne
    @JoinColumn(name = "user_id", nullable=true)
    @OnDelete(action = OnDeleteAction.SET_NULL) // Kullanıcı silindiğinde user_id NULL yapılır
    @JsonIgnore
    private User user;

//	@ManyToOne(fetch = FetchType.LAZY) // Content çektiğimizde user objesi gelmesin diye
    @ManyToOne
    @JoinColumn(name = "content_id", nullable=false)

    @JsonIgnore
    private Content content;
	
    
 // Yorum sahibinin ID'sini döndür
    public Long getUserId() {
        return user != null ? user.getId() : null; // Eğer user null değilse, user ID'sini döndürüyoruz
    }
    
    @Column(name = "user_deleted")
    private boolean userDeleted;  // Yorumun, silinen kullanıcıya ait olup olmadığını belirtmek için
	
}
