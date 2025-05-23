package com.example.demo.form;

import com.example.demo.entity.City;

import lombok.Data;

@Data
public class CityForm {

    private Long id;
    private String name;
    
    
    public static CityForm from(City city) {
        CityForm dto = new CityForm();
        dto.setId(city.getId());
        dto.setName(city.getName());
        return dto;
    }
}
