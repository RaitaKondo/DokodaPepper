package com.example.demo.form;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommentReturnForm {

    private Long commentId;
    
    private String username;
    
    private String content;
    
    private LocalDateTime created_at;
    
    public CommentReturnForm(Long commentId, String username, String content, LocalDateTime created_at) {
        this.commentId = commentId;
        this.username = username;
        this.content = content;
        this.created_at = created_at;
    }
}
