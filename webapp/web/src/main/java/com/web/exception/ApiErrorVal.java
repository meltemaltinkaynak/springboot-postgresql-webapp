package com.web.exception;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ApiErrorVal<T> {
	
//	private String id;
//	
//	private Date errorTime;
	
	private T errors;

}
