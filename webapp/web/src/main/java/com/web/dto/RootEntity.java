package com.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RootEntity<T> {

private boolean result;  //servis başarılı oldu mu olmadı mı
	
	private String errorMessage; // result false ise neden başarsız olduğunu 
	
	private  T data; // eğer başarılı olduysa bu değişkende tut, bu data'nın tipi belli olmaz, bu yüzden generic

	
	//Başarılıysa başarılı bir şekilde döndüren metot
	public static <T> RootEntity<T> ok(T data){  //Eğer bu metodu çağırırsak sen dışardan bir data ver, çübkü alabilmişsin datayı
		RootEntity<T> rootEntity = new RootEntity<>();  // dönüş
		rootEntity.setData(data);  //parametre olarak gelen data
		rootEntity.setResult(true); // ok cağırdıgım için başarılı
		rootEntity.setErrorMessage(null);  // hata olmadıgı için null
		
		return rootEntity;
	}
	
	
//	//Hatalıysa hatalı şekilde döndüren metot

	
	public static <T> RootEntity<T> error(String errorMessage, T data) {
	    RootEntity<T> rootEntity = new RootEntity<>();
	    rootEntity.setData(data);  // Hata ile ilgili ekstra veriyi ekle
	    rootEntity.setErrorMessage(errorMessage); 
	    rootEntity.setResult(false);
	    
	    return rootEntity;
	}

	
}
