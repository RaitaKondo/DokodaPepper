package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.FoundIt;
import com.example.demo.entity.FoundItId;
import com.example.demo.repository.FoundItRepository;

@Service
public class FoundItService {

    private final FoundItRepository foundItRepository;
    
    public FoundItService (FoundItRepository foundItRepository) {
        this.foundItRepository = foundItRepository;
    }
    
    public List<FoundIt> getAllFoundIts(){
        return foundItRepository.findAll();
    }
    
    // この引数は複合主キー。定義はしたがよくわからない。
    public FoundIt getFoundItById(FoundItId id) {
        return foundItRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FoundIt not found"));
    }
}
