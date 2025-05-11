package com.web.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ErrorMessage {
	
	// oluşturmuş oldgumuz enumı burada veriyoruz
	private MessageType messageType;
		
	private String ofStatic; //Static olarak kullanıcının vereceği değer
		
	// Gelen mesaj tipini burada yönetiyoruz:
		
	// Bu method, enumdaki mesajı alıp dönecek
	public String prepareErrorMessage() {
			
		StringBuilder builder = new StringBuilder();  //builder adında bir StringBuilder nesnesi oluşturuyoruz
		builder.append(messageType.getMessage());  // messageType'ın içerirsindeki message'ı alıyoruz
		if(ofStatic!=null) {  //ofStatic null değilse
			builder.append(" : " + ofStatic); // ofStatic'i de ekliyoruz
		}
			
		return builder.toString();  //mesajı string olarak dön
	}
		

}
