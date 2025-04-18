package com.web.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class DtoUserLike {

    
    private Long contentId;
    private String contentTitle;
    private LocalDateTime likeCreatedAt;
    
    

}
