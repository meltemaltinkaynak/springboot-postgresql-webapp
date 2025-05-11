package com.web.service.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dto.DtoComment;
import com.web.dto.DtoCommentIU;
import com.web.dto.DtoContentComment;
import com.web.dto.DtoLike;
import com.web.dto.DtoLikeIU;
import com.web.dto.DtoUserComment;
import com.web.dto.DtoUserCommentList;
import com.web.dto.DtoUserLike;
import com.web.exception.BaseException;
import com.web.exception.ErrorMessage;
import com.web.exception.MessageType;
import com.web.model.Comment;
import com.web.model.Content;
import com.web.model.Like;
import com.web.model.User;
import com.web.repository.ContentRepository;
import com.web.repository.LikeRepository;
import com.web.repository.UserRepository;
import com.web.service.ILikeService;

@Service
public class LikeServiceImpl implements ILikeService {

	@Autowired
	private LikeRepository likeRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContentRepository contentRepository;
	
	
	
	
	// Tüm likeları getirme
	
	@Override
	public List<DtoLike> getAllLikes() {
		List<DtoLike> response = new ArrayList<>();
			
		List<Like> likeList = likeRepository.findAll();
			
		for (Like like : likeList) {
			DtoLike dtoLike = new DtoLike();
			BeanUtils.copyProperties(like, dtoLike);
				
			// userId'yi ver
		     if (like.getUser() != null) {
		        dtoLike.setUserId(like.getUser().getId());
		     }
		        
		    // conetndId'yi ver
		    if (like.getContent() != null) {
		        dtoLike.setContentId(like.getContent().getId());
		    }

			response.add(dtoLike);
				
		}
		return response;
	}
	
	
	// User id ile içerik listesi
	 @Override
	 public List<DtoUserLike> getLikesByUserId(Long userId) {	 
		 
		 	Optional<User> optional = userRepository.findById(userId);
			
			if(optional.isEmpty()) {
				throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, userId.toString()));
			}
			
			  // Kullanıcının yorumlarını getir
		    List<DtoUserLike> comments = likeRepository.findLikesByUserId(userId);
			
			// İçeriklerin olup olmadığını kontrol et
		    for (DtoUserLike comment : comments) {
		        if (comment.getContentId() == null) {
		            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Yorum yapılan içerik bulunamadı."));
		        }
		    }
		    
			 // Beğenileri getir
	        List<DtoUserLike> likes = likeRepository.findLikesByUserId(userId);
			
	        // Eğer istersen beğeni yoksa hata fırlatabilirsin
	        if (likes.isEmpty()) {
	            throw new BaseException(new ErrorMessage(MessageType.NO_LIKES_FOUND, "Kullanıcının beğenisi yok."));
	        }
		 
	        return likes;
	    }
	
	
	
	
	
	// like id  ile like getirme	
	@Override
	public DtoLike getOneLike(Long likeId) {
		DtoLike response = new DtoLike();
			
		Optional<Like> optional = likeRepository.findById(likeId);
			
		if(optional.isEmpty()) {
			throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, likeId.toString()));
		}
			
			
		Like dbLike= optional.get();
		BeanUtils.copyProperties(dbLike, response);
			
		// userId'yi manuel olarak set et		
	    if (dbLike.getUser() != null) {
	        response.setUserId(dbLike.getUser().getId());
	    }
		    
		// contentId'yi manuel olarak set et		
		if (dbLike.getContent() != null) {
		     response.setContentId(dbLike.getContent().getId());
		}
		    
		return response;
			
     }
	
	
//	Like oluşturma	
	
	@Override
	public DtoLike toggleLike(Long contentId, Long userId) {
	    DtoLike response = new DtoLike();

	    User dbUser = userRepository.findById(userId)
	        .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, userId.toString())));

	    Content dbContent = contentRepository.findById(contentId)
	        .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, contentId.toString())));

	    Optional<Like> existingLike = likeRepository.findByUser_IdAndContent_Id(userId, contentId);

	    if (existingLike.isPresent()) {
	        // Beğeni varsa sil
	        likeRepository.delete(existingLike.get());
	        response.setMessage("Beğeni kaldırıldı.");
	    } else {
	        // Beğeni yoksa ekle
	        Like like = new Like();
	        like.setUser(dbUser);
	        like.setContent(dbContent);
	        likeRepository.save(like);

	        response.setUserId(userId);
	        response.setContentId(contentId);
	        response.setMessage("Beğeni eklendi.");
	    }

	    return response;
	}

	
	
	
	
	
	
		
		
		//içerik id ile beğenileri getirme
		@Override
		public List<DtoLike> getLikesByContentId(Long contentId) {
		    // İçeriği veritabanında bul
		    Optional<Content> optionalContent = contentRepository.findById(contentId);
		    
		    if (optionalContent.isEmpty()) {
		        throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, contentId.toString()));
		    }
		    
		    Content content = optionalContent.get();

		    // İçeriğin beğenilerini DTO'ya çevir
		    return content.getLikes().stream().map(like -> {
		        DtoLike dto = new DtoLike();
		        dto.setLikeId(like.getId());
		        dto.setUserId(like.getUser() != null ? like.getUser().getId() : -1L);
		        dto.setContentId(like.getContent().getId());
		        dto.setCreatedAt(like.getCreatedAt());  // Beğeninin yapıldığı zamanı ekle
		        
		        return dto;
		    }).collect(Collectors.toList());
		}


		@Override
		public int getLikeCountByContentId(Long contentId) {
		    // İçeriği veritabanında bul
		    Optional<Content> optionalContent = contentRepository.findById(contentId);

		    // Eğer içerik bulunamazsa hata fırlat
		    if (optionalContent.isEmpty()) {
		        throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, contentId.toString()));
		    }

		    Content content = optionalContent.get();

		    // İçeriğin beğenilerini al ve toplam beğeni sayısını hesapla
		    return content.getLikes().size(); // Beğeni sayısını döndür
		}

		
		// kullanıcı ieçrik beğen durumu
		@Override
		public boolean isContentLikedByUser(Long contentId, Long userId) {
		    return likeRepository.existsByContentIdAndUser_Id(contentId, userId);
		}

		

}
