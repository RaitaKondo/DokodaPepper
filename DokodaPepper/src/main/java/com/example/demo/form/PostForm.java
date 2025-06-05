package com.example.demo.form;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class PostForm {

    @NotBlank(message = "タイトルは必須です")
    @Size(max = 50, message = "タイトルは50文字以下で入力してください")
    private String content;
    private Double latitude;
    private Double longitude;
    private String address;
    private Long cityId;
    private Long prefectureId;
    private List<MultipartFile> images;

}
