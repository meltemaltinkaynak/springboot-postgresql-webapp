package com.web.exception;

import lombok.Getter;

@Getter
public enum MessageType {

	
	//Böyle bir kayıt yok, hiçbir exception tipine uymuyorsa bunu general'a fırlat
	NO_RECORD_EXIST("1001", "KAYIT BULUNAMADI"),  // bu enum'ın içine vermiş olduğum ilk değer code, ikinci değer message
	GENERAL_EXCEPTION("9999", "GENEL BİR HATA OLUŞTU"),  
	NO_NOTIFICATIONS("1002", "BU KULLANICI İÇİN BİLDİRİM BULUNAMADI."),
	UNAUTHORIZED_ACTION("2002", " Yetkiniz yok"),
	USER_NOT_FOUND("1003", "Kullanıcı e-posta ile bulunamadı."),
	INVALID_PARAMETER("1001", "KAYIT BULUNAMADI"),
	NO_LIKES_FOUND("1005","BEGENİ BULUNAMADI"),
	NO_COMMENTS_FOUND("1006","YORUM BULUNAMADI"),
	ALREADY_EXISTS("1004","Bu içeriği zaten beğendiniz.");
		
	private String code;
	private String message;
		
		
	MessageType(String code, String message) {
		// Yukarıda tanımladığımız code'u, parametre olarak gelen code''a setle
		this.code = code;
		this.message = message;
	}
		
}
