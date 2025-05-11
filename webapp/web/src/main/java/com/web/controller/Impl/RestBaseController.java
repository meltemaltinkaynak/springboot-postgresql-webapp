package com.web.controller.Impl;

import com.web.dto.RootEntity;

public class RestBaseController {

	//T tipinde data alıp, rootentity dönecek
	public <T> RootEntity<T> ok(T data){
		return RootEntity.ok(data);  // almış oldugu datayı root entitideki ok metoduna ver ve burada rootEntity datayla beraber oluştu ve vermiş oldugu tipte bir tane rootEntitty oluşturarak dönüş oldu 
	}

		
	public <T> RootEntity<T> Error(String errorMessage){
		return RootEntity.error(errorMessage, null);
	}
}
