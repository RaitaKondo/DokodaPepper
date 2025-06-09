package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Data
@Entity
@Table(name = "reports")
public class Report {

    @EmbeddedId
    private ReportId id;
    
    @ManyToOne
    @MapsId("userId")  // ReportIdの中のuserIdとマッピング
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @MapsId("postId")  // ReportIdの中のpostIdとマッピング
    @JoinColumn(name = "post_id")
    private Post post;
    
    @Column(name = "reported_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime reportAt; 
    
    public Report() {
        // デフォルトコンストラクタ
    }
    
    public Report(User user, Post post) {
        this.id = new ReportId();
        this.id.setUserId(user.getId());
        this.id.setPostId(post.getId());
        this.user = user;
        this.post = post;
    }
}
