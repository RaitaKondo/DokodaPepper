package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Area;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
    // 追加のクエリメソッドをここに記述

    Optional<Area> findByName(String name);
}
