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
import com.web.dto.DtoContent;
import com.web.dto.DtoContentComment;
import com.web.dto.DtoContentIU;
import com.web.dto.DtoUserComment;
import com.web.dto.DtoUserCommentList;
import com.web.exception.BaseException;
import com.web.exception.ErrorMessage;
import com.web.exception.MessageType;
import com.web.model.Comment;
import com.web.model.Content;
import com.web.model.Role;
import com.web.model.User;
import com.web.repository.CommentRepository;
import com.web.repository.ContentRepository;
import com.web.repository.UserRepository;
import com.web.service.ICommentService;


@Service
public class CommentServiceImpl implements ICommentService{

	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContentRepository contentRepository;
	
	
	
	
	//userId ile yorumlar
	@Override
	public List<DtoUserCommentList> getCommentsByUserId(Long userId) {

	    // Kullanıcıyı kontrol et
	    Optional<User> optionalUser = userRepository.findById(userId);

	    if (optionalUser.isEmpty()) {
	        throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Kullanıcı bulunamadı: " + userId));
	    }

	    // Kullanıcının yorumlarını getir
	    List<DtoUserCommentList> comments = commentRepository.getUserComments(userId);

	    if (comments.isEmpty()) {
	        throw new BaseException(new ErrorMessage(MessageType.NO_COMMENTS_FOUND, "Kullanıcının yorumu bulunmamaktadır."));
	    }

	    // İçeriklerin olup olmadığını kontrol et
	    for (DtoUserCommentList comment : comments) {
	        if (comment.getContentId() == null) {
	            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Yorum yapılan içerik bulunamadı."));
	        }
	    }

	    return comments;
	}


	
	
	// Tüm yorumları getirme
	
	@Override
	public List<DtoComment> getAllComments() {
		List<DtoComment> response = new ArrayList<>();
		
		List<Comment> commentList = commentRepository.findAll();
		
		for (Comment comment : commentList) {
			DtoComment dtoComment = new DtoComment();
			BeanUtils.copyProperties(comment, dtoComment);
			
			// userId'yi ver
	        if (comment.getUser() != null) {
	        	dtoComment.setUserId(comment.getUser().getId());
	        }
	        
	        // conetndId'yi ver
	        if (comment.getContent() != null) {
//	        	dtoComment.setContentId(comment.getContent().getId());
	        }

			response.add(dtoComment);
			
		}
		return response;
	}
	
	
	// Yorum id'si ile yorum getirme
	
	@Override
	public DtoComment getOneComment(Long commentId) {
		DtoComment response = new DtoComment();
		
		Optional<Comment> optional = commentRepository.findById(commentId);
		
		if(optional.isEmpty()) {
			throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, commentId.toString()));
		}
		
		
		Comment dbComment= optional.get();
		BeanUtils.copyProperties(dbComment, response);
		
		// userId'yi manuel olarak set et		
	    if (dbComment.getUser() != null) {
	        response.setUserId(dbComment.getUser().getId());
	    }
	    
	    // contentId'yi manuel olarak set et		
	    if (dbComment.getContent() != null) {
//	        response.setContentId(dbComment.getContent().getId());
	    }
	    
	   
		return response;
		
	}
	
	// Content id ile yorum getirme
	@Override
	public DtoContentComment getComment(Long contentId) {
		DtoContentComment response = new DtoContentComment();
	    
	    // Content'i veritabanından bul
	    Optional<Content> optional = contentRepository.findById(contentId);
	    if (optional.isEmpty()) {
	        throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, contentId.toString()));
	    }
	    
	    Content content = optional.get();
	    
	    // İçeriğe ait yorumları al
	    List<Comment> dbCommentList = content.getComments();
	    
	    // Eğer yorum yoksa hata fırlat
	    if (dbCommentList == null || dbCommentList.isEmpty()) { 
	        throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, contentId.toString()));
	    }
	    
	    // DTO nesnesine yorumları ekle
	    for (Comment comment : dbCommentList) {
	        DtoComment dtoComment = new DtoComment();
	        
	        BeanUtils.copyProperties(comment, dtoComment); 
	        
	        dtoComment.setUserId(comment.getUser().getId());  // Kullanıcı ID'yi ekle
//	        dtoComment.setContentId(comment.getContent().getId());  // İçerik ID'yi ekle
	        
	        response.getComments().add(dtoComment);
	    }
	    
//	    response.setCommentCount(dbCommentList.size());
	    
	    return response;
	}
	
	
	
	// yorum silme
	@Override
	public void deleteComment(Long userId, Long commentId) {
		
		
		Optional<Comment> optional = commentRepository.findById(commentId);
		if (optional.isEmpty()) {
		    throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, commentId.toString()));
		}

		Comment dbComment = optional.get();
		
		

		// Yorumun sahibi ile şu anki kullanıcıyı karşılaştırıyoruz
	    if (!dbComment.getUserId().equals(userId)) {
	        throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED_ACTION, "Yetkiniz yok."));
	    }

		commentRepository.delete(dbComment);
		
	}

	
	//Content id ile yorum listesi
	@Override
	public List<DtoComment> getCommentsByContentId(Long contentId) {
		 // İçeriği veritabanında bul
	    Optional<Content> optionalContent = contentRepository.findById(contentId);
	    
	    if (optionalContent.isEmpty()) {
	        throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, contentId.toString()));
	    }
	    
	    Content content = optionalContent.get();

	    // İçeriğin yorumlarını DTO'ya çevir
	    return content.getComments().stream().map(comment -> {
	        DtoComment dto = new DtoComment();
	        dto.setCommentId(comment.getId());
	        dto.setText(comment.getText());
	        dto.setCreatedAt(comment.getCreatedAt());
	        dto.setUserId(comment.getUser().getId());
	      
	        return dto;
	    }).collect(Collectors.toList());
	}
		
	@Override
	//content id ile yorum sayısı
	 public int getCommentCountByContentId(Long contentId) {
		
			if (contentId == null) {
			    throw new BaseException(new ErrorMessage(MessageType.INVALID_PARAMETER, "Content ID null olamaz"));
			}
	        // İçeriği veritabanından bul
	        Optional<Content> optionalContent = contentRepository.findById(contentId);

	        // Eğer içerik bulunamazsa hata fırlat
	        if (optionalContent.isEmpty()) {
	            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, contentId.toString()));
	        }

	        Content content = optionalContent.get();

	        // İçeriğin yorumlarını al ve toplam yorum sayısını hesapla
	        return content.getComments().size(); // Yorum sayısını döndür
	   }
	
	
	// Yorum oluşturma
		@Override
		public DtoComment saveComment( Long contentId,Long userId, DtoCommentIU dtoCommentIU) {
		    DtoComment response = new DtoComment();

		    // Kullanıcıyı kontrol et
		    Optional<User> optional = userRepository.findById(userId);
		    if (optional.isEmpty()) {
		        throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, userId.toString()));
		    }

		    User dbUser = optional.get();


		    // İçeriği kontrol et
		    Optional<Content> optionalContent = contentRepository.findById(contentId);
		    if (optionalContent.isEmpty()) {
		        throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, contentId.toString()));
		    }

		    Content dbContent = optionalContent.get();
		   

		 
		    Comment comment = new Comment();
		    BeanUtils.copyProperties(dtoCommentIU, comment);
		    comment.setUser(dbUser);
		    comment.setContent(dbContent); // Yorum içeriğe bağlanıyor

		    
		    Comment dbComment = commentRepository.save(comment);

		   
		    BeanUtils.copyProperties(dbComment, response);
		    response.setUserId(dbComment.getUser().getId());
//		    response.setContentId(dbComment.getContent().getId());

		    return response;
		}


	
	
}	

