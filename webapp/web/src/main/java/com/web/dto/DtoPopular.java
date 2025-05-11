package com.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoPopular {
    private Long contentId;
    private String title;
    private String photo;
    private boolean isRestricted;
    private int viewCount;
    private int likeCount;
    private int commentCount;
}