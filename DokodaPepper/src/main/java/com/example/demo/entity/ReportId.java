package com.example.demo.entity;

import jakarta.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class ReportId {
    private Long userId;
    private Long postId;

    public ReportId() {
        // デフォルトコンストラクタ
    }

    public ReportId(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
    }

}
