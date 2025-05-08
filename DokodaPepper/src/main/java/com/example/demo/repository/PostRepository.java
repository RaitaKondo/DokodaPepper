package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // Postエンティティに関連するクエリメソッドをここに記述
    // 例: List<Post> findByTitleContaining(String title);
    // 例: List<Post> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    

    @Query(value = """
                    SELECT * FROM posts
                    WHERE user_id = 1
                    	        """, nativeQuery = true)
    Post findSinglePost();
    
    List<Post> findTop6ByOrderByCreatedAtDesc();
    
    Page<Post> findAll(Pageable pageable);
    

}

