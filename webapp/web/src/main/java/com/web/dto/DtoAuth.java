package com.web.dto;

import com.web.model.Role;

import lombok.Data;

@Data
public class DtoAuth {
	
	private Long userId;
	
	private String firstName;
	
	private String lastName;

	private String redirectUrl;
	
	private Role role;
}
