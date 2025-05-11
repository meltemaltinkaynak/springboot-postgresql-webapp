package com.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionRequest {
	  	private Long userId;
	    private String ipAddress;
	    private String deviceName;
	    

}
