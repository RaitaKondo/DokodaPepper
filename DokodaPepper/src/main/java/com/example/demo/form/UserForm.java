package com.example.demo.form;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UserForm {

    @NotNull
    private String username;
    @NotNull
    private String password;

    // 追加のフィールドやメソッドをここに記述

}
