package com.web.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class DtoComment {

	private Long commentId;
    private String text;
    private LocalDateTime createdAt;
    private Long userId;   
//    private Long contentId; 
    

   
}
