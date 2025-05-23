package com.example.demo.form;

import com.example.demo.entity.Prefecture;

import lombok.Data;

@Data
public class PrefectureForm {
    private Long id;
    private String name;

    // Entity → DTO 変換メソッド
    public static PrefectureForm from(Prefecture e) {
        PrefectureForm dto = new PrefectureForm();
        dto.setId(e.getId());
        dto.setName(e.getName());
        return dto;
    }
    
}
