package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FoundIt;
import com.example.demo.entity.FoundItId;

@Repository
public interface FoundItRepository extends JpaRepository<FoundIt, FoundItId> {
    // FoundItIdをキーとして使用するため、FoundItIdを指定
    // 追加のクエリメソッドをここに記述
    long countByPost_Id(Long postId);


}
