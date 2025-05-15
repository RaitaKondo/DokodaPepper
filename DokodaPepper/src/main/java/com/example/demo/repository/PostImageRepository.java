package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.PostImage;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    // PostImageに関連するクエリメソッドをここに記述
    // 例: List<PostImage> findByPostId(Long postId);
    // 例: void deleteByPostId(Long postId);

}
