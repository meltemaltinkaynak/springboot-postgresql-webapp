package com.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class DtoLogin {
	
	@NotBlank(message = "Email alanı boş bırakılamaz.")
	@Email(message = "Geçerli bir email adresi giriniz.")
	private String email;
	
	@NotBlank(message = "Şifre alanı boş bırakılamaz.")
	@Size(min = 10, message = "Şifre en az 10 karakter olmalıdır.")
	private String password;

}
