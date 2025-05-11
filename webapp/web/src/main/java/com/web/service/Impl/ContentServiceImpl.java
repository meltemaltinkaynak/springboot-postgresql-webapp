package com.web.service.Impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.web.dto.DtoComment;
import com.web.dto.DtoContent;
import com.web.dto.DtoContentIU;
import com.web.dto.DtoMostInteractedContent;
import com.web.dto.DtoMostViewedContent;
import com.web.dto.DtoPopular;
import com.web.dto.DtoStatistics;
import com.web.dto.DtoUser;
import com.web.dto.DtoUserContent;
import com.web.dto.DtoUserProfil;
import com.web.dto.DtoUserStatistics;
import com.web.exception.BaseException;
import com.web.exception.ErrorMessage;
import com.web.exception.MessageType;
import com.web.model.Content;
import com.web.model.ContentCategory;
import com.web.model.Role;
import com.web.model.User;
import com.web.repository.CommentRepository;
import com.web.repository.ContentRepository;
import com.web.repository.LikeRepository;
import com.web.repository.UserRepository;
import com.web.service.IContentService;

import io.jsonwebtoken.io.IOException;

@Service
public class ContentServiceImpl implements IContentService{

	@Autowired
	private ContentRepository  contentRepository;

	@Autowired
	private UserRepository userRepository;
	
	 @Autowired
	 private LikeRepository likeRepository;

	 @Autowired
	 private CommentRepository commentRepository;
	
	@Autowired
	private FileStorageServiceImpl fileStorageService;
	
	
	@Override
	public DtoUserStatistics getUserStatistics(Long userId) {
	    DtoUserStatistics statisticsDTO = new DtoUserStatistics();

	    Optional<User> userOptional = userRepository.findById(userId);
	    if (userOptional.isEmpty()) {
	        throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, userId.toString()));
	    }

	    User user = userOptional.get();

	    long totalContentCount = contentRepository.countByUser(user);
	    long restrictedContentCount = contentRepository.countByUserAndIsRestricted(user, true);
	    long totalLikes = likeRepository.countLikesByUser(user);
	    long totalComments = contentRepository.findByUser(user).stream()
	                                          .mapToLong(content -> commentRepository.countCommentsByContent(content))
	                                          .sum();
	    long totalViews = contentRepository.findByUser(user).stream()
	                                       .mapToLong(Content::getViewCount)
	                                       .sum();

	    List<Content> mostInteractedContents = contentRepository.findMostInteractedContent(user);

	    Content mostInteractedContent = null;
	    if (!mostInteractedContents.isEmpty()) {
	        mostInteractedContent = mostInteractedContents.get(0);
	    }

	    DtoMostInteractedContent mostInteractedContentDTO = null;
	    if (mostInteractedContent != null) {
	        mostInteractedContentDTO = new DtoMostInteractedContent();
	        mostInteractedContentDTO.setContentId(mostInteractedContent.getId());
	        mostInteractedContentDTO.setContentTitle(mostInteractedContent.getTitle());

	        long likeCount = likeRepository.countLikesByContent(mostInteractedContent);
	        long commentCount = commentRepository.countCommentsByContent(mostInteractedContent);
	        long viewCount = mostInteractedContent.getViewCount();

	        mostInteractedContentDTO.setLikeCount(likeCount);
	        mostInteractedContentDTO.setCommentCount(commentCount);
	        mostInteractedContentDTO.setViewCount((long) viewCount);
	        mostInteractedContentDTO.setTotalInteraction(likeCount + commentCount + viewCount);
	    }

	    statisticsDTO.setTotalContentCount(totalContentCount);
	    statisticsDTO.setRestrictedContentCount(restrictedContentCount);
	    statisticsDTO.setTotalLikes(totalLikes);
	    statisticsDTO.setTotalComments(totalComments);
	    statisticsDTO.setTotalViews(totalViews);
	    statisticsDTO.setMostInteractedContent(mostInteractedContentDTO);

	    return statisticsDTO;
	}

	
	
	
	// Tüm içerikleri getirme
	@Override
	public List<DtoContent> getAllContents() {
		List<DtoContent> response = new ArrayList<>();
		
		List<Content> contentList = contentRepository.findAll();
		
		for (Content content : contentList) {
			DtoContent dtoContent = new DtoContent();
			BeanUtils.copyProperties(content, dtoContent);
			
			// userId'yi ver
	        if (content.getUser() != null) {
	            dtoContent.setUserId(content.getUser().getId());
	        }

			response.add(dtoContent);
			
		}
		return response;
	}
	
	
	// userId  ile içerik getirme
	
	@Override
	public List<DtoContent> getContentsByUserId(Long userId) {
	    // Kullanıcıyı kontrol et
	    Optional<User> optionalUser = userRepository.findById(userId);
	    if (optionalUser.isEmpty()) {
	        throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, userId.toString()));
	    }
	    
	    // Kullanıcı varsa, içeriği getir
	    List<Content> contents = contentRepository. findByUserIdOrderByCreatedAtDesc(userId);
	    if (contents.isEmpty()) {
	        throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, userId.toString())); // Eğer içerik yoksa hata fırlat
	    }

	    // İçerikleri dönüştür
	    return contents.stream().map(content -> {
	        DtoContent dto = new DtoContent();
	        dto.setContentId(content.getId());
	        dto.setTitle(content.getTitle());
	        dto.setPhoto(content.getPhoto());
	        dto.setText(content.getText());
	        dto.setRestricted(content.isRestricted());
	        dto.setCreatedAt(content.getCreatedAt());
	        dto.setUserId(content.getUser().getId());
	        dto.setCategory(content.getCategory());
	        dto.setViewCount(content.getViewCount());
	        
	        return dto;
	    }).collect(Collectors.toList());
	}


	
	
	// contendId ile içerik getirme
	@Override
	public DtoContent getContentByContentId(Long contentId) {
	    // İçeriği içerik ID'si ile bul
	    Optional<Content> optionalContent = contentRepository.findById(contentId);
	    if (optionalContent.isEmpty()) {
	        throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, contentId.toString()));
	    }

	    // İçeriği dönüştür
	    Content content = optionalContent.get();
	    DtoContent dto = new DtoContent();
	    dto.setContentId(content.getId());
	    dto.setTitle(content.getTitle());
	    dto.setPhoto(content.getPhoto());
	    dto.setText(content.getText());
	    dto.setRestricted(content.isRestricted());
	    dto.setCreatedAt(content.getCreatedAt());
	    dto.setUserId(content.getUser().getId());
	    dto.setCategory(content.getCategory());
	    
	    return dto;
	}
	
	
	// Misafir kullanıcı sadece kısıtlı içerikleri görebilir
	
	@Override
	public DtoContent getContentByContentIdForGuest(Long contentId) {
	    Optional<Content> optionalContent = contentRepository.findById(contentId);

	    if (optionalContent.isEmpty()) {
	        throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, contentId.toString()));
	    }

	    Content content = optionalContent.get();

	    if (content.isRestricted()) {
	        // Kısıtlı içeriğe misafir erişmeye çalıştı
	        throw new BaseException(new ErrorMessage(MessageType.ACCESS_DENIED, contentId.toString()));
	    }

	    // DTO'ya dönüştür
	    DtoContent dto = new DtoContent();
	    dto.setContentId(content.getId());
	    dto.setTitle(content.getTitle());
	    dto.setPhoto(content.getPhoto());
	    dto.setText(content.getText());
	    dto.setRestricted(content.isRestricted());
	    dto.setCreatedAt(content.getCreatedAt());
	    dto.setUserId(content.getUser().getId());
	    dto.setCategory(content.getCategory());

	    return dto;
	}


	
	// Görüntülenme sayısına göre ilk 3
	@Override
	public List<DtoMostViewedContent> getTop3MostViewedContent() {
	    List<Content> topContents = contentRepository.findTop3ByOrderByViewCountDesc();
	    
	    return topContents.stream().map(content -> {
	        DtoMostViewedContent dto = new DtoMostViewedContent();
	        dto.setContentId(content.getId());
	        dto.setTitle(content.getTitle());
	        dto.setUserId(content.getUser().getId());
	        dto.setCreatedAt(content.getCreatedAt());
	        dto.setViewCount(content.getViewCount());
	        return dto;
	    }).collect(Collectors.toList());
	}
	
	
	//popular  - giriş yapmamış
	@Override
	public List<DtoPopular> getTop10Popular() {
	    return contentRepository.findTop10Popular(PageRequest.of(0, 10))
	            .stream()
	            .map(content -> {
	                String photo = content.isRestricted()
	                        ? "/uploads/public/placeholder-restricted.png"
	                        : content.getPhoto();
	                return new DtoPopular(
	                        content.getId(),
	                        content.getTitle(),
	                        photo,
	                        content.isRestricted(),
	                        content.getViewCount(),
	                        content.getLikes() != null ? content.getLikes().size() : 0,
	                        content.getComments() != null ? content.getComments().size() : 0
	                );
	            })
	            .collect(Collectors.toList());
	}

	@Override
	public List<DtoPopular> getTop10ByLikes() {
	    return contentRepository.findTop10ByLikes(PageRequest.of(0, 10))
	            .stream()
	            .map(content -> {
	                String photo = content.isRestricted()
	                        ? "/uploads/public/placeholder-restricted.png"
	                        : content.getPhoto();
	                return new DtoPopular(
	                        content.getId(),
	                        content.getTitle(),
	                        photo,
	                        content.isRestricted(),
	                        content.getViewCount(),
	                        content.getLikes() != null ? content.getLikes().size() : 0,
	                        content.getComments() != null ? content.getComments().size() : 0
	                );
	            })
	            .collect(Collectors.toList());
	}

	@Override
	public List<DtoPopular> getTop10ByComments() {
	    return contentRepository.findTop10ByComments(PageRequest.of(0, 10))
	            .stream()
	            .map(content -> {
	                String photo = content.isRestricted()
	                        ? "/uploads/public/placeholder-restricted.png"
	                        : content.getPhoto();
	                return new DtoPopular(
	                        content.getId(),
	                        content.getTitle(),
	                        photo,
	                        content.isRestricted(),
	                        content.getViewCount(),
	                        content.getLikes() != null ? content.getLikes().size() : 0,
	                        content.getComments() != null ? content.getComments().size() : 0
	                );
	            })
	            .collect(Collectors.toList());
	}

	@Override
	public List<DtoPopular> getTop10ByViews() {
	    return contentRepository.findTop10ByViews(PageRequest.of(0, 10))
	            .stream()
	            .map(content -> {
	                String photo = content.isRestricted()
	                        ? "/uploads/public/placeholder-restricted.png"
	                        : content.getPhoto();
	                return new DtoPopular(
	                        content.getId(),
	                        content.getTitle(),
	                        photo,
	                        content.isRestricted(),
	                        content.getViewCount(),
	                        content.getLikes() != null ? content.getLikes().size() : 0,
	                        content.getComments() != null ? content.getComments().size() : 0
	                );
	            })
	            .collect(Collectors.toList());
	}

	//Popular- giriş yapmış
	@Override
    public List<DtoPopular> getTop10PopularPrivate() {
        return contentRepository.findTop10Popular(PageRequest.of(0, 10))
                .stream()
                .map(content -> new DtoPopular(
                        content.getId(),
                        content.getTitle(),
                        content.getPhoto(),
                        content.isRestricted(),
                        content.getViewCount(),
                        content.getLikes() != null ? content.getLikes().size() : 0,
                        content.getComments() != null ? content.getComments().size() : 0
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<DtoPopular> getTop10ByLikesPrivate() {
        return contentRepository.findTop10ByLikes(PageRequest.of(0, 10))
                .stream()
                .map(content -> new DtoPopular(
                        content.getId(),
                        content.getTitle(),
                        content.getPhoto(),
                        content.isRestricted(),
                        content.getViewCount(),
                        content.getLikes() != null ? content.getLikes().size() : 0,
                        content.getComments() != null ? content.getComments().size() : 0
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<DtoPopular> getTop10ByCommentsPrivate() {
        return contentRepository.findTop10ByComments(PageRequest.of(0, 10))
                .stream()
                .map(content -> new DtoPopular(
                        content.getId(),
                        content.getTitle(),
                        content.getPhoto(),
                        content.isRestricted(),
                        content.getViewCount(),
                        content.getLikes() != null ? content.getLikes().size() : 0,
                        content.getComments() != null ? content.getComments().size() : 0
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<DtoPopular> getTop10ByViewsPrivate() {
        return contentRepository.findTop10ByViews(PageRequest.of(0, 10))
                .stream()
                .map(content -> new DtoPopular(
                        content.getId(),
                        content.getTitle(),
                        content.getPhoto(),
                        content.isRestricted(),
                        content.getViewCount(),
                        content.getLikes() != null ? content.getLikes().size() : 0,
                        content.getComments() != null ? content.getComments().size() : 0
                ))
                .collect(Collectors.toList());
    }
    
    
    
    
	
	// ieçrik yükleme
	@Override
	public DtoContent saveContent(Long userId, String title, String text, ContentCategory category, String imageUrl, boolean isRestricted) {
	    DtoContent response = new DtoContent();

	    // Kullanıcıyı veritabanından al
	    Optional<User> optional = userRepository.findById(userId);
	    if (optional.isEmpty()) {
	        throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, userId.toString()));
	    }

	    User dbUser = optional.get();

	    // Kullanıcı rolünü kontrol et
	    if (dbUser.getRole() == Role.USER) {
	        throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED_ACTION, userId.toString()));
	    }

	    // İçeriği oluştur
	    Content content = new Content();
	    content.setTitle(title);
	    content.setText(text);
	    content.setCategory(category);
	    content.setRestricted(isRestricted);  // İçeriğin restricted durumunu ayarla

	    // Fotoğraf varsa, URL'yi içerikte sakla
	    if (imageUrl != null && !imageUrl.isEmpty()) {
	        content.setPhoto(imageUrl);  // Fotoğrafın URL'sini içerikte sakla
	    }

	    content.setUser(dbUser);

	    // İçeriği veritabanına kaydet
	    Content dbContent = contentRepository.save(content);

	    // Yanıt DTO'sunu oluştur ve döndür
	    BeanUtils.copyProperties(dbContent, response);
	    response.setUserId(dbContent.getUser().getId());
	    response.setPhoto(dbContent.getPhoto());  // URL'yi yanıt DTO'suna ekle

	    return response;
	}




	
//	// İçerik güncelleme
//	@Override
//	public DtoContent updateContent(Long userId, Long contentId, DtoContentIU dtoContentIU) {
//	    DtoContent response = new DtoContent();
//	    
//	    Optional<Content> optional = contentRepository.findById(contentId);
//	    
//	    if (optional.isEmpty()) {
//	        throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, contentId.toString()));
//	    }
//	    
//	    Content dbContent = optional.get();
//
//	    // Kullanıcının içerik üzerinde güncelleme yapmaya yetkisi olup olmadığını kontrol et
//	    if (!canPerformContentAction(userId, contentId)) {
//	        throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED_ACTION, contentId.toString()));
//	    }
//	    
//	    // İçerik güncelleniyor
//	    dbContent.setTitle(dtoContentIU.getTitle());
//	    dbContent.setPhoto(dtoContentIU.getPhoto());
//	    dbContent.setText(dtoContentIU.getText());
//	    
//	    Content updatedContent = contentRepository.save(dbContent);
//	    
//	    BeanUtils.copyProperties(updatedContent, response);
//	    response.setUserId(updatedContent.getUser().getId());
//	    
//	    return response;
//	}

	@Override
	public Map<String, Object> patchContent(Long userId, Long contentId, Map<String, Object> updates) {
	    Optional<Content> optional = contentRepository.findById(contentId);
	    if (optional.isEmpty()) {
	        throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, contentId.toString()));
	    }

	    Content dbContent = optional.get();
	    List<String> updatedFields = new ArrayList<>();

	    // Kullanıcının içerik üzerinde güncelleme yapmaya yetkisi olup olmadığını kontrol et
	    if (!canPerformContentAction(userId, contentId)) {
	        throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED_ACTION, contentId.toString()));
	    }

	    // Sadece gönderilen alanları güncelle
	    updates.forEach((key, value) -> {
	        switch (key) {
	            case "title":
	                dbContent.setTitle((String) value);
	                updatedFields.add("title");
	                break;
	            case "photo":
	                dbContent.setPhoto((String) value);
	                updatedFields.add("photo");
	                break;
	            case "text":
	                dbContent.setText((String) value);
	                updatedFields.add("text");
	                break;
	        }
	    });

	    contentRepository.save(dbContent);

	    Map<String, Object> response = new HashMap<>();
	    response.put("updatedFields", updatedFields);

	    return response;
	}



	

	// İçerik silme
	@Override
	public void deleteContent(Long userId, Long contentId) {
	    Optional<Content> optional = contentRepository.findById(contentId);
	    if (optional.isEmpty()) {
	        throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, contentId.toString()));
	    }

	    Content dbContent = optional.get();

	    // Eğer kullanıcı silme, güncelleme veya oluşturma yetkisine sahip değilse hata fırlat
	    if (!canPerformContentAction(userId, contentId)) {
	        throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED_ACTION, contentId.toString()));
	    }

	    contentRepository.delete(dbContent);
	}

	
	
	// Kullanıcının içerik oluşturma, güncelleme veya silme yetkisini kontrol etme
	private boolean canPerformContentAction(Long userId, Long contentId) {
	    Optional<User> userOptional = userRepository.findById(userId);
	    if (!userOptional.isPresent()) {
	        return false;
	    }

	    User user = userOptional.get();
	    
	    // Eğer kullanıcı Content Admin rolünde ise sadece kendi içeriğini silebilir veya güncelleyebilir
	    if (user.getRole() == Role.CONTENTADMIN) {
	        Optional<Content> contentOptional = contentRepository.findById(contentId);
	        if (!contentOptional.isPresent()) {
	            return false;
	        }
	        Content content = contentOptional.get();
	        return content.getUser().getId().equals(userId); 
	    }

	    // Eğer kullanıcı Super Admin ise tüm içerikleri oluşturabilir, güncelleyebilir veya silebilir
	    if (user.getRole() == Role.SUPERADMIN) {
	        return true;
	    }

	    return false; // Diğer durumlarda yani rol user ise hiçbir işlem yapamaz
	}




	// içerik kısıtı güncelleme
	@Override
	public boolean updateContentRestriction(Long contentId, boolean isRestricted, Long userId) {
		
		
		Optional<User> optionalUser = userRepository.findById(userId);
		    if (optionalUser.isEmpty()) {
		        throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, userId.toString()));
		    }

		User dbUser = optionalUser.get();
        // Kullanıcının SUPER_ADMIN olup olmadığını kontrol et
        if (!dbUser.getRole().equals(Role.SUPERADMIN)) {
        	throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED_ACTION, userId.toString()));
        }

        Optional<Content> optionalContent = contentRepository.findById(contentId);
		
		if(optionalContent.isEmpty()) {
			throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, contentId.toString()));
		}

		Content dbContent = optionalContent.get();
		dbContent.setRestricted(isRestricted);
        contentRepository.save(dbContent);

        return dbContent.isRestricted();
    }
	
	
	// kategoriye göre içerik getirme
	@Override
	public List<DtoContent> getContentsByCategory(ContentCategory category) {
		
		List<DtoContent> response = new ArrayList<>();
		
		List<Content> contentList = contentRepository.findByCategory(category);
		
		for (Content content : contentList) {
			DtoContent dtoContent = new DtoContent();
			BeanUtils.copyProperties(content, dtoContent);
			
			// userId'yi ver
	        if (content.getUser() != null) {
	            dtoContent.setUserId(content.getUser().getId());
	        }

			response.add(dtoContent);
			
		}		
		
        return response;
    }
	
	// son  bir haftanın içeriklerini getirme- kısıtlı 
	@Override
	public List<DtoContent>  getRecentContents() {
		 LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
		 List<Content> contents = contentRepository.findByCreatedAtAfterOrderByCreatedAtDesc(oneWeekAgo);
		 return contents.stream().map(content -> {
		        DtoContent dto = new DtoContent();
		        dto.setContentId(content.getId());
		        dto.setTitle(content.getTitle());
		        dto.setPhoto(content.getPhoto());
		        dto.setText(content.getText());
		        dto.setRestricted(content.isRestricted());
		        dto.setCreatedAt(content.getCreatedAt());
		        dto.setUserId(content.getUser().getId());
		        dto.setCategory(content.getCategory());
		        
		        return dto;
		    }).collect(Collectors.toList());
	}
//	
//	// son 4 içerik
//	@Override
//	public List<DtoContent> getRecentFourContents() {
//	    List<Content> contents = contentRepository.findTop4ByOrderByCreatedAtDesc();
//	    return contents.stream().map(content -> {
//	        DtoContent dto = new DtoContent();
//	        dto.setContentId(content.getId());
//	        dto.setTitle(content.getTitle());
//	       
//	        dto.setText(content.getText());
//	        dto.setRestricted(content.isRestricted());
//	        dto.setCreatedAt(content.getCreatedAt());
//	        dto.setUserId(content.getUser().getId());
//	        dto.setCategory(content.getCategory());
//	        return dto;
//	    }).collect(Collectors.toList());
//	}

	// son 4 içerik
	@Override
	public List<DtoContent> getRecentFourContents() {
	    List<Content> contents = contentRepository.findTop4ByOrderByCreatedAtDesc();
	    return contents.stream().map(content -> {
	        DtoContent dto = new DtoContent();
	        dto.setContentId(content.getId());
	        dto.setTitle(content.getTitle());

	        // Kısıtlı içerikse placeholder göster, değilse gerçek fotoğraf yolu
	        if (content.isRestricted()) {
	            dto.setPhoto("/uploads/public/placeholder-restricted.png");
	            dto.setText("İçeriğe erişebilmek için giriş yapmanız gerekmektedir. Kayıt işlemini gerçekleştirmediyseniz, lütfen kaydolun.");
	        } else {
	            dto.setPhoto(content.getPhoto());
	            dto.setText(content.getText());
	        }

	        dto.setRestricted(content.isRestricted());
	        dto.setCreatedAt(content.getCreatedAt());
	        dto.setUserId(content.getUser().getId());
	        dto.setCategory(content.getCategory());
	        return dto;
	    }).collect(Collectors.toList());
	}
	
	// son 4 içerik
		@Override
		public List<DtoContent> getRecentFourContentsPrivate() {
		    List<Content> contents = contentRepository.findTop4ByOrderByCreatedAtDesc();
		    return contents.stream().map(content -> {
		        DtoContent dto = new DtoContent();
		        dto.setContentId(content.getId());
		        dto.setTitle(content.getTitle());		       
		        dto.setPhoto(content.getPhoto());
		        dto.setText(content.getText());		      
		        dto.setRestricted(content.isRestricted());
		        dto.setCreatedAt(content.getCreatedAt());
		        dto.setUserId(content.getUser().getId());
		        dto.setCategory(content.getCategory());
		        return dto;
		    }).collect(Collectors.toList());
		}
		
	
	
	// İçeriğin görüntülenme sayısını artıran metod
    public void incrementViewCount(Long contentId) {
        // İçeriği veritabanından al
        Content content = contentRepository.findById(contentId)
            .orElseThrow(() -> new RuntimeException("İçerik bulunamadı"));

        // viewCount değerini artır
        content.setViewCount(content.getViewCount() + 1);

        // Güncellenmiş içeriği kaydet
        contentRepository.save(content);
    }
	
	// son  bir haftanın içeriklerini getirme- kısıtsız-public
	@Override
	public List<DtoContent> getRecentUnrestrictedContents() {
	    LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
	    List<Content> contents = contentRepository.findByCreatedAtAfterAndIsRestrictedOrderByCreatedAtDesc(oneWeekAgo, false);

	    return contents.stream().map(content -> {
	        DtoContent dto = new DtoContent();
	        dto.setContentId(content.getId());
	        dto.setTitle(content.getTitle());
	        dto.setPhoto(content.getPhoto());
	        dto.setText(content.getText());
	        dto.setRestricted(content.isRestricted());
	        dto.setCreatedAt(content.getCreatedAt());
	        dto.setUserId(content.getUser().getId());
	        dto.setCategory(content.getCategory());
	        return dto;
	    }).collect(Collectors.toList());
	}





	// kategoriye göre  tüm içerikler
	@Override
	public List<DtoContent> getContentsByCategoryAndNoRestriction(ContentCategory category) {
		
		List<Content> contents = contentRepository.findByCategoryOrderByCreatedAtDesc(category);
		
	    return contents.stream().map(content -> {
	        DtoContent dto = new DtoContent();
	        dto.setContentId(content.getId());
	        dto.setTitle(content.getTitle());
	        dto.setPhoto(content.getPhoto());
	        dto.setText(content.getText());
	        dto.setRestricted(content.isRestricted());
	        dto.setCreatedAt(content.getCreatedAt());
	        dto.setUserId(content.getUser().getId());
	        dto.setCategory(content.getCategory());
	        
	        return dto;
	    }).collect(Collectors.toList());
	}


	
	// kısıtlı  içerikleri gösterme
	@Override
	public List<DtoContent> getContentsByCategoryAndRestricted(ContentCategory category, boolean isRestricted) {
		List<Content> contents = contentRepository.findByCategoryAndIsRestrictedOrderByCreatedAtDesc(category, isRestricted);
	    return contents.stream().map(content -> {
	        DtoContent dto = new DtoContent();
	        dto.setContentId(content.getId());
	        dto.setTitle(content.getTitle());
	        dto.setPhoto(content.getPhoto());
	        dto.setText(content.getText());
	        dto.setRestricted(content.isRestricted());
	        dto.setCreatedAt(content.getCreatedAt());
	        dto.setUserId(content.getUser().getId());
	        dto.setCategory(content.getCategory());
	        return dto;
	    }).collect(Collectors.toList());
	}
	
	
	
}
	
	

