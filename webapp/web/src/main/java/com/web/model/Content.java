package com.web.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Content")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Content {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "title", nullable = false, length = 1000)
	private String title;
	
	@Column(name = "photo")
	private String photo;
	

	@Column(name = "text" , nullable = false,columnDefinition = "TEXT") //TEXT veri türü,  PostgreSQL'de uzun metinleri saklamak için
	private String text;
	
	@Column(name = "is_restricted")
	private boolean isRestricted;
	
	@Enumerated(EnumType.STRING) 
    @Column(name = "category")
    private ContentCategory category;
	
	@Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    @Column(name = "view")
    private int viewCount = 0;  // Başlangıçta 0
    
    @Column(name = "enabled")
    private boolean enabled = true; // Varsayılan olarak true

    
//  @ManyToOne(fetch = FetchType.LAZY) // Content çektiğimizde user objesi gelmesin diye
    @ManyToOne
    @JoinColumn(name = "user_id", nullable=true)
    @OnDelete(action = OnDeleteAction.SET_NULL) // Kullanıcı silindiğinde user_id NULL yapılır
    @JsonIgnore
    private User user;  // Kullanıcı ile ilişki
	
 // Comment ile ilişki
    @OneToMany(mappedBy = "content") 			
 
	private List<Comment> comments;  
    
 // Like ile ilişki
    @OneToMany(mappedBy = "content")	
 
	private List<Like> likes;  


    @Column(name = "user_deleted")
    private boolean userDeleted;  // Kullanıcı silindiyse, içeriğin silinmediğini ancak "Kullanıcı Silinmiş" olarak işaretlendiğini belirten alan
}
