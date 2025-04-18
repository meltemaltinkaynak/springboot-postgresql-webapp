package com.web.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class DtoUserIU {

	
	@NotEmpty(message = "Ad alanı boş bırakılamaz.")
	@Size(min =1, max = 30,  message="Ad en az 1, en fazla 30 karakter  uzunluğunda olmalıdır.")
    private String firstName;
	
	@NotEmpty(message = "Soyad alanı boş bırakılamaz.")
	@Size(min =1, max = 30,  message="Soyad en az 1, en fazla 30 karakter uzunluğunda  olmalıdır.")
	@Column(name = "last_name")
    private String lastName;
	
	@NotEmpty(message = "E-posta alanı boş bırakılamaz.")
	@Email(message= "Geçerli bir e-posta adresi giriniz.")
    private String email;
	
	@Size(min = 10, max = 255 , message="Şifre en az 10 karakter uzunluğunda olmalıdır.")
	@Column(name = "password")
    private String password;

	
	@NotEmpty(message = " Şifre tekrar alanı boş bırakılamaz.")
	private String confirmPassword;
}
