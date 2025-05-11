package com.web.dto;

import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.time.LocalDateTime;
import java.util.List;

import com.web.model.User;
import com.web.model.ContentCategory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class DtoContent {
	
	private Long contentId;
	
	private String title;
	
	private String photo;
	
	private String text;
	
	private boolean isRestricted;
	
	private LocalDateTime createdAt;
	
	private Long userId;
	
	private ContentCategory category;
	
	private int viewCount;
	
//	private String firstName;  
//    private String lastName;
//    
//    private int likeCount;
//    private int commentCount;
//    
//    private List<DtoComment> comments; // Yorumları içerecek liste
    
	
	
	
	
	

}
