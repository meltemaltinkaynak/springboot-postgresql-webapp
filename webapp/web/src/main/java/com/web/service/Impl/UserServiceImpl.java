package com.web.service.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.web.dto.DtoAuthor;
import com.web.dto.DtoContent;
import com.web.dto.DtoUser;
import com.web.dto.DtoUserContent;
import com.web.dto.DtoUserIU;
import com.web.dto.DtoUserProfil;
import com.web.exception.BaseException;
import com.web.exception.ErrorMessage;
import com.web.exception.MessageType;
import com.web.model.Content;
import com.web.model.Role;
import com.web.model.User;
import com.web.repository.UserRepository;
import com.web.service.IUserService;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements IUserService{

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	// User kayıt
	@Transactional
    @Override
    public DtoUser saveUser(DtoUserIU dtoUserIU) {
        DtoUser response = new DtoUser();
        User user = new User();

        // Eğer kullanıcı zaten varsa hata fırlat
        User existingUser = userRepository.findByEmail(dtoUserIU.getEmail());
        if (existingUser != null) {
            throw new IllegalArgumentException("Bu e-posta adresi zaten bir kullanıcı tarafından kullanılmaktadır.");
        }

        // Şifreler uyuşuyor mu?
        if (!dtoUserIU.getPassword().equals(dtoUserIU.getConfirmPassword())) {
            throw new IllegalArgumentException("Girilen şifreler eşleşmemektedir. Lütfen kontrol ediniz.");
        }

        // Kullanıcı bilgilerini kopyala
        BeanUtils.copyProperties(dtoUserIU, user);

        // Şifreyi hashle
        user.setPassword(passwordEncoder.encode(dtoUserIU.getPassword()));

        // Varsayılan olarak "USER" rolü ata
        user.setRole(Role.USER);

        // Kullanıcıyı veritabanına kaydet
        User dbUser = userRepository.save(user);
        BeanUtils.copyProperties(dbUser, response);

        return response;
    }
	
	
	// Kullanıcı id'si ile kullanıcı profili
	@Override
	public DtoUserProfil getUserProfile(Long id) {
		DtoUserProfil response = new DtoUserProfil();
		
		Optional<User> optional = userRepository.findById(id);
		
		if(optional.isEmpty()) {
			throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, id.toString()));
		}
		
		
		User dbUser = optional.get();
		BeanUtils.copyProperties(dbUser, response);
		
	
		return response;
	}


	// Superadmin için tüm kullanıcıların listesi
	@Override
	public List<DtoUser> getUserList() {
		List<DtoUser> response = new ArrayList<>();
		
		List<User> userList = userRepository.findAll();
		
		for (User user : userList) {
			DtoUser dtoUser = new DtoUser();
			BeanUtils.copyProperties(user, dtoUser);
			response.add(dtoUser);
			
		}
		return response;
	}


	// User delete
	@Override
	public void deleteUser(Long id) {
		Optional<User> optional = userRepository.findById(id);
		if(optional.isEmpty()) {
			throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, id.toString()));
		}
		userRepository.delete(optional.get());
		
		
	}


	// User update
	// Kullanıcı bilgileri güncelleme (şifre yok)
	@Override
	public Map<String, Object> patchUser(Long userId, Map<String, Object> updates) {
	    Optional<User> optional = userRepository.findById(userId);
	    if (optional.isEmpty()) {
	        throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, userId.toString()));
	    }

	    User dbUser = optional.get();
	    List<String> updatedFields = new ArrayList<>();

	    // Sadece gönderilen alanları güncelle
	    updates.forEach((key, value) -> {
	        switch (key) {
	            case "firstName":
	                dbUser.setFirstName((String) value);
	                updatedFields.add("firstName");
	                break;
	            case "lastName":
	                dbUser.setLastName((String) value);
	                updatedFields.add("lastName");
	                break;
	            
	            case "profilePhoto":
	                dbUser.setProfilePhoto((String) value);
	                updatedFields.add("profilePhoto");
	                break;
	        }
	    });

	    userRepository.save(dbUser);

	    Map<String, Object> response = new HashMap<>();
	    response.put("updatedFields", updatedFields);

	    return response;
	}


	
	
	@Override
	public User getOneUserByEmail(String email) {
		// TODO Auto-generated method stub
		return userRepository.findByEmail(email);
	}
	
	@Override
	public User getOneUserById(Long id) {
	    return userRepository.findById(id).orElse(null);
	}



	
	@Override
	// userId ile  içeriğin yazarı
	public DtoAuthor getAuthorById(Long userId) {
		DtoAuthor response = new DtoAuthor();
		
		Optional<User> optional = userRepository.findById(userId);
		
		if(optional.isEmpty()) {
			throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, userId.toString()));
		}
		
		
		User dbUser = optional.get();
		BeanUtils.copyProperties(dbUser, response);
		return response;
	}

	
	


	
	
	
	
	
	
	
	
	
}
