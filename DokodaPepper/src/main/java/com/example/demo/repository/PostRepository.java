package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // Postエンティティに関連するクエリメソッドをここに記述
    // 例: List<Post> findByTitleContaining(String title);
    // 例: List<Post> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

}
