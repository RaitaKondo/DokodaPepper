package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{


    List<Comment> findByPostOrderByCreatedAtDesc(Post post);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.user.id = :userId AND c.createdAt >= :thresholdTime")
    int countRecentComments(@Param("userId") Long userId, @Param("thresholdTime") LocalDateTime thresholdTime);

}
