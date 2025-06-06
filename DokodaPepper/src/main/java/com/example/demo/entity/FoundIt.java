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

@Entity
@Data
@Table(name = "found_it")
public class FoundIt {

    @EmbeddedId
    private FoundItId id; // ← ここがポイント！

    @ManyToOne
    @MapsId("userId")  // FoundItIdの中のuserIdとマッピング
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("postId")  // FoundItIdの中のpostIdとマッピング
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "found_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime foundAt;
    
    public FoundIt() {
    }
    
    public FoundIt(User user, Post post) {
        this.id = new FoundItId();
        this.id.setUserId(user.getId());
        this.id.setPostId(post.getId());
        this.user = user;
        this.post = post;
    }
}
