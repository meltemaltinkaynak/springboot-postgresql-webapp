package com.web.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoMostViewedContent {
	
    private Long contentId;
    private String title;
    private String authorFullName;
    private LocalDateTime createdAt;
    private int viewCount;
    private Long userId;

}
