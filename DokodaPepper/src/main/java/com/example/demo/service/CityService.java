package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.demo.form.CityForm;
import com.example.demo.repository.CityRepository;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    /**
     * /api/prefectures で都道府県一覧を取得したあと、 React側で選択されたprefIdを指定して市区町村をロード
     */
    
    @Cacheable(value = "citiesByPref", key = "#prefId")
    public List<CityForm> fetchByPrefecture(Integer prefId) {
        return cityRepository.findByPrefectureId(prefId)
                         .stream()
                         .map(CityForm::from)
                         .collect(Collectors.toList());
    }
}
