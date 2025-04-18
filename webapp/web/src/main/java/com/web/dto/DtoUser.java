package com.web.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.web.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoUser {
	
	private Long id;
	
	private String firstName;
	
	private String lastName;
	 
	private Role role; 
	


}
