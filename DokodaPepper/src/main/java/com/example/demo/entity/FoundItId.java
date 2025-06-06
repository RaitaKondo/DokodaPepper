package com.example.demo.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class FoundItId implements Serializable {
    private Long userId;
    private Long postId;
    
    public FoundItId() {
        // デフォルトコンストラクタ
    }

    public FoundItId(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
    }
}
