package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.demo.form.PrefectureForm;
import com.example.demo.repository.PrefectureRepository;

@Service
public class PrefectureService {

    private final PrefectureRepository prefectureRepository;
    
    public PrefectureService(PrefectureRepository prefectureRepository) {
        this.prefectureRepository = prefectureRepository;
    }

    @Cacheable("prefectures")
    public List<PrefectureForm> fetchAll() {
        // DB から一度だけ読み込む
        return prefectureRepository.findAll().stream()
                         .map(PrefectureForm::from)
                         .collect(Collectors.toList());
    }
}
