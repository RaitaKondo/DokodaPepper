package com.example.demo.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class CommentForm {

    @NotBlank(message="タイトルは必須です。")
    @Size(max=50, message="50文字以内でお願いします。")
    private String content;
}
