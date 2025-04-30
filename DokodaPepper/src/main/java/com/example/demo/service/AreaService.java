package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Area;
import com.example.demo.repository.AreaRepository;

@Service
public class AreaService {

    private final AreaRepository areaRepository;
    
    public AreaService(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }
    
    public List<Area> getAllAreas() {
        return areaRepository.findAll();
    }
    
    public Area getAreabyId(Long id) {
        return areaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Area not found"));
    }
}
