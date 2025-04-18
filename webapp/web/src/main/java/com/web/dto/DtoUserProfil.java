package com.web.dto;

import java.time.LocalDateTime;

import com.web.model.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoUserProfil {

    private Long id;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String profilePhoto; 
	 
	private Role role; 
	
	private LocalDateTime createdAt;
	
	

}
