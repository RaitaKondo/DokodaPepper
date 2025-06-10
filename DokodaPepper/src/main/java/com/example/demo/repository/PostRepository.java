package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Post;
import com.example.demo.entity.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // Postエンティティに関連するクエリメソッドをここに記述
    // 例: List<Post> findByTitleContaining(String title);
    // 例: List<Post> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Post> findByUser(User user);

    

    @Query(value = """
                    SELECT * FROM posts
                    WHERE user_id = 1
                    	        """, nativeQuery = true)
    Post findSinglePost();
    
    List<Post> findTop6ByOrderByCreatedAtDesc();
    
    Page<Post> findAll(Pageable pageable);
    
    Page<Post> findByPrefectureId(Long id, Pageable pageable);
    
    Page<Post> findByCity_Id(Long cityId, Pageable pageable);


    @Query("SELECT p FROM Post p WHERE p.user = :user ORDER BY p.createdAt DESC LIMIT 1")
    Optional<Post> findLatestByUser(@Param("user") User user);

}

