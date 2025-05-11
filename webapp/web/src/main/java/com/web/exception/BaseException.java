package com.web.exception;

public class BaseException  extends RuntimeException{
	
//	//Bir tane parametresiz Constructor açıyoruz. Burada almış oldugumuz değeri RuntimeException'a vereceğiz.	
//	public BaseException() {
//			
//	}
//		
//		
//	// Bir tane de parametreli const acıyoruz
//	// Dışardan bir ERrorMessage alcak ve  bunu
//	public BaseException(ErrorMessage errorMessage) {
//		//dışardan gelen errorMessageın içinde bir prepareErrrorMEssage() methodu var bu methodu kullanarak hata mesajını hazırla
//		// hata mesajını bana dön ve dönmüş oldugun değeri de super methodu kullanarak RuntimeExceptionun baseine gönder
//		super(errorMessage.prepareErrorMessage());
//			
//	}
	
	
	private final ErrorMessage errorMessage; // ❗ Bu alanı ekle

    public BaseException() {
        this.errorMessage = null;
    }

    public BaseException(ErrorMessage errorMessage) {
        super(errorMessage.prepareErrorMessage());
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return this.errorMessage;
    }

}
