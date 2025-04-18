package com.web.exception;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.web.dto.RootEntity;







@RestControllerAdvice 
public class GlobalExceptionHandler {

	//----- Spring validation'dan fırlatılan hataları yönetmek	
	//Map'e değer ekleme:
	private List<String> addMapValue(List<String> list, String newValue){
		list.add(newValue);
		return list;
	}
	
//	@ExceptionHandler(value = MethodArgumentNotValidException.class) 
	
//	public ResponseEntity<ApiErrorVal> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
//		// map tanımlama
//		Map<String, List<String>> errorsMap = new HashMap<>();  // şu formda olacak: email: "hata1","hata2","hata3" gibi
//		for (ObjectError objError : ex.getBindingResult().getAllErrors()) {  
//			
//			//  objError'u FieldError tipine cevirme
//			 String fieldName= ((FieldError)objError).getField(); 
//			 if(errorsMap.containsKey(fieldName)) {  
//				 errorsMap.put(fieldName,addMapValue(errorsMap.get(fieldName),objError.getDefaultMessage()));
//			 }else { //errorsMapin içindeki containsKey fieldName'i içermiyorsa yani yeni bir değer ise
//				 errorsMap.put(fieldName, addMapValue(new ArrayList<>(), objError.getDefaultMessage())); // sıfırdan bir array oluştur, o hata mesajını koy
//			 }
//			
//			
//		}
//	return ResponseEntity.badRequest().body(createApiError(errorsMap));
//	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<RootEntity<Map<String, List<String>>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
	    Map<String, List<String>> errorsMap = new HashMap<>();

	    for (ObjectError objError : ex.getBindingResult().getAllErrors()) {
	        String fieldName = ((FieldError) objError).getField();
	        errorsMap.computeIfAbsent(fieldName, key -> new ArrayList<>()).add(objError.getDefaultMessage());
	    }

	    return ResponseEntity.badRequest().body(RootEntity.error("Validation Hatası", errorsMap));
	}


	
	
	private <T> ApiErrorVal<T> createApiError(T errors) {
		ApiErrorVal<T> apiError = new ApiErrorVal<T>();
		
		apiError.setErrors(errors);
		
		return apiError;
		
	}
	
	
	
	//BaseExceptionları yakalayabilmek için
	@ExceptionHandler(value= {BaseException.class})  // yani BaseException türünde bir hata fırlatıyorum, bu exceptionu tut.
	public ResponseEntity<ApiError> handleBaseException(BaseException exception, WebRequest request) {  // oradan fırlatılan excceptionu burada parametre olarak geç
			
		return ResponseEntity.badRequest().body(createApiError(exception.getMessage(), request));
			
			
	}
		

		
		
	// ApiError tipinde dönecek
	public <E> ApiError<E> createApiError(E message, WebRequest request){
		ApiError<E> apiError = new ApiError<>(); //Api Error oluşturuyoruz	
			
		Exception<E> exception = new Exception<>(); //exception nesnesi oluşturuyoruz			
		exception.setMessage(message);
		apiError.setException(exception);
			
		return apiError;
	
	}
		


}
