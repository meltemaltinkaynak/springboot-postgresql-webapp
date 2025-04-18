package com.web.dto;

import java.time.LocalDateTime;

import com.web.model.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter

public class DtoUserCommentList {
	
		private Long contentId;
	    private String contentTitle;
	    private String commentText;
	    private LocalDateTime createdAt;

}
