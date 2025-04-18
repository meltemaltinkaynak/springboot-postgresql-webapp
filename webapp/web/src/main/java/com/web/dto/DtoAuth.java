package com.web.dto;

import lombok.Data;

@Data
public class DtoAuth {
	
	private Long userId;
	
	private String firstName;
	
	private String lastName;

	private String redirectUrl;
}
