package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FoundIt;
import com.example.demo.entity.FoundItId;
import com.example.demo.entity.Post;

@Repository
public interface FoundItRepository extends JpaRepository<FoundIt, FoundItId> {
    // FoundItIdをキーとして使用するため、FoundItIdを指定
    // 追加のクエリメソッドをここに記述
    long countByPost_Id(Long postId);

    @Query("SELECT f.post FROM FoundIt f WHERE f.user.id = :userId")
    List<Post> findPostsByUserId(@Param("userId") Long userId);

}
