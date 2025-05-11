package com.web.exception;

import java.util.Date;

import lombok.Data;

@Data
public class Exception<E> {
	

	private E message;  // hangi tip gelirse o tip olacak

}
