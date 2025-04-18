package com.web.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.web.model.Content;
import com.web.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoUserStatistics {
	
	 	private Long totalContentCount;
	    private Long restrictedContentCount;
	    private Long totalLikes;
	    private Long totalComments;
	    private DtoMostInteractedContent mostInteractedContent;

}
