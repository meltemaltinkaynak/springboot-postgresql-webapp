package com.web.dto;

import java.time.LocalDateTime;


import com.web.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class DtoNotification {
	
    private Long id;
    private String message;
    private boolean isRead;
    private NotificationType type;
    private LocalDateTime createdAt;
   

}
