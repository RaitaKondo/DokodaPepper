package com.example.demo.form;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class PostForm {

    private String content;
    private Double latitude;
    private Double longitude;
    private String address;
    private Long cityId;
    private List<MultipartFile> images;

}
