package com.example.demo.form;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

import com.example.demo.entity.City;
import com.example.demo.entity.PostImage;

import lombok.Data;

@Data
public class PostReturnForm {

    private String userName;
    
    private Long postId;

    private City city;

    private String prefectureName;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PostImage> images = new java.util.ArrayList<>();

    private Double latitude;

    private Double longitude;

    private String address;
}
