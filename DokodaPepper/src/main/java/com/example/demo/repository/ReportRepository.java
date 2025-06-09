package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Post;
import com.example.demo.entity.Report;
import com.example.demo.entity.ReportId;

@Repository
public interface ReportRepository extends JpaRepository<Report, ReportId> {
    // FoundItIdをキーとして使用するため、FoundItIdを指定
    // 追加のクエリメソッドをここに記述
    long countByPost_Id(Long postId);

    @Query("SELECT r.post FROM Report r WHERE r.user.id = :userId")
    List<Post> findPostsByUserId(@Param("userId") Long userId);

}
