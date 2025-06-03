package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Prefecture;

@Repository
public interface PrefectureRepository extends JpaRepository<Prefecture, Long> {
    // Prefectureエンティティに関連するクエリメソッドをここに記述
    // 例: List<Prefecture> findByNameContaining(String name);
    // 例: List<Prefecture> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    Optional<Prefecture> findById(Long id); // 名前で都道府県を検索するメソッド
}
