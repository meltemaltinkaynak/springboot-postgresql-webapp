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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "like")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Like {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	
	
	@Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
	
	@PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
	
	@Column(name = "enabled")
    private boolean enabled = true; // Varsayılan olarak true
	

    @ManyToOne
    @JoinColumn(name = "user_id", nullable=true)
    @OnDelete(action = OnDeleteAction.SET_NULL) // Kullanıcı silindiğinde user_id NULL yapılır
  
    @JsonIgnore
    private User user;


    @ManyToOne
    @JoinColumn(name = "content_id", nullable=false)
//    @OnDelete(action = OnDeleteAction.CASCADE) // bir contetn silindiğinde tüm yorumlar da silinsin
    @JsonIgnore
    private Content content;

	public Long getUserId() {
		
		 return user != null ? user.getId() : null;
	}
	
	@Column(name = "user_deleted")
    private boolean userDeleted;  // Beğeninin, silinen kullanıcıya ait olup olmadığını belirtmek için

}
