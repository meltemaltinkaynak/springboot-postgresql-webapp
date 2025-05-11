package com.web.exception;

import com.web.exception.Exception;

import lombok.Data;

@Data
public class ApiError <E>{
	
	private Exception<E> exception;

}
