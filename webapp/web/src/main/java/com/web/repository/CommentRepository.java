package com.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.web.dto.DtoUserCommentList;
import com.web.model.Comment;
import com.web.model.Content;
import com.web.model.User;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{
	
	
	@Query("SELECT new com.web.dto.DtoUserCommentList(c.content.id, c.content.title, c.text, c.createdAt) " +
		       "FROM Comment c WHERE c.user.id = :userId")
		List<DtoUserCommentList> getUserComments(@Param("userId") Long userId);
	
	long countCommentsByUser(Optional<User> user);
    long countCommentsByContent(Content content);

}
