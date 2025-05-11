package com.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoChangePassword {
	
	
	 @NotEmpty(message = "Mevcut şifre boş bırakılamaz.")
	 private String currentPassword;

	 @Size(min = 10, max = 255, message = "Yeni şifre en az 10 karakter olmalıdır.")
	 private String newPassword;

	  @NotEmpty(message = "Yeni şifre tekrarı boş bırakılamaz.")
	  private String confirmNewPassword;

}
