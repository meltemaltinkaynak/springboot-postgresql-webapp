package com.web.service.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.web.dto.DtoAuthor;
import com.web.dto.DtoChangePassword;
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
import com.web.repository.CommentRepository;
import com.web.repository.ContentRepository;
import com.web.repository.LikeRepository;
import com.web.repository.UserRepository;
import com.web.security.JwtTokenProvider;
import com.web.service.IUserService;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements IUserService{

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
    private JwtTokenProvider jwtTokenProvider;
	
	 @Autowired
	 private LikeRepository likeRepository;

	 @Autowired
	 private CommentRepository commentRepository;
	 
	 @Autowired
	 private ContentRepository contentRepository;
	
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
	
	
	// şifre değiştirme
	
	@Transactional
	@Override
	public DtoUser changePassword(Long userId, DtoChangePassword dtoChangePassword) {
	    // Kullanıcıyı bul
	    User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı"));

	    // Mevcut şifreyi doğrula
	    if (!passwordEncoder.matches(dtoChangePassword.getCurrentPassword(), user.getPassword())) {
	        throw new IllegalArgumentException("Mevcut şifre hatalı.");
	    }

	    // Yeni şifreler eşleşiyor mu?
	    if (!dtoChangePassword.getNewPassword().equals(dtoChangePassword.getConfirmNewPassword())) {
	        throw new IllegalArgumentException("Yeni şifreler eşleşmiyor.");
	    }

	    // Yeni şifrenin güçlü olup olmadığını kontrol et (Opsiyonel)
	    if (dtoChangePassword.getNewPassword().length() < 10) {
	        throw new IllegalArgumentException("Yeni şifre en az 10 karakter olmalıdır.");
	    }

	    // Yeni şifreyi hash'le
	    user.setPassword(passwordEncoder.encode(dtoChangePassword.getNewPassword()));

	    // Kullanıcıyı kaydet
	    User updatedUser = userRepository.save(user);

	    DtoUser response = new DtoUser();
	    BeanUtils.copyProperties(updatedUser, response);
	    
	    return response;
	}

	//Anonim hesap
	@PostConstruct
	public void createAnonUserIfNotExists() {
	    if (!userRepository.existsById(-1L)) {
	        User anon = new User();
	        anon.setId(-1L); // Manuel ID ataması
	        anon.setFirstName("Silinmiş");
	        anon.setLastName("Kullanıcı");
	        anon.setEmail("deleted@user.local");
	        anon.setPassword("anon"); // Şifrelenmiş olmasına gerek yok
	        anon.setRole(Role.USER);  // Gerekirse USER rolü

	        userRepository.save(anon);
	    }
	}
	
	//Hesap silme
	@Override
	@Transactional
	public void deleteUser(Long userId, boolean deleteContent) {
	    User user = userRepository.findById(userId)
	        .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı"));

	    if (deleteContent) {
	        // İçerikleri, beğenileri ve yorumları tamamen sil
	        contentRepository.deleteAll(user.getContent());
	        likeRepository.deleteAll(user.getLike());
	        commentRepository.deleteAll(user.getComment());
	    } else {
	        // Anonim kullanıcıyı veritabanından bul
	        User anonUser = userRepository.findById(-1L)
	            .orElseThrow(() -> new IllegalStateException("Anonim kullanıcı bulunamadı (id = -1)."));

	        // İçerikleri anonim kullanıcıya aktar
	        user.getContent().forEach(content -> {
	            content.setUser(anonUser);
	            content.setUserDeleted(true);
	        });

	        user.getLike().forEach(like -> {
	            like.setUser(anonUser);
	            like.setUserDeleted(true);
	        });

	        user.getComment().forEach(comment -> {
	            comment.setUser(anonUser);
	            comment.setUserDeleted(true);
	        });

	        contentRepository.saveAll(user.getContent());
	        likeRepository.saveAll(user.getLike());
	        commentRepository.saveAll(user.getComment());
	    }

	    // Kullanıcıyı sistemden kaldır
	    userRepository.delete(user);
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



	// Yazar adı soyadı
	@Override
	public DtoAuthor getAuthorById(Long userId) {
	    DtoAuthor response = new DtoAuthor();

	    Optional<User> optional = userRepository.findById(userId);

	    if (optional.isEmpty()) {
	        throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, userId.toString()));
	    }

	    User dbUser = optional.get();

	    // Eğer kullanıcı devre dışıysa, adı ve soyadı 'Bilinmeyen Kullanıcı' olarak ayarla
	    if (!dbUser.isEnabled()) {
	        response.setFirstName("Bilinmeyen");
	        response.setLastName("Kullanıcı");
	    } else {
	        BeanUtils.copyProperties(dbUser, response);
	    }

	    return response;
	}


	
	


	
	
	
	
	
	
	
	
	
}
