package com.example.demo.controller.api;

import java.time.Duration;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.form.PrefectureForm;
import com.example.demo.service.PrefectureService;

@RestController
public class PrefectureController {
    private final PrefectureService prefectureService;

    PrefectureController(PrefectureService prefectureService){
        this.prefectureService = prefectureService;
    }
    
    @GetMapping("/api/prefectures")
    public ResponseEntity<List<PrefectureForm>> getPrefectures(){
        
        List<PrefectureForm> list = prefectureService.fetchAll();
        // キャッシュを使用しているため、DBからの読み込みは1回だけ
        String eTag = "\"" + DigestUtils.md5Hex(list.toString()) + "\"";
        
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(Duration.ofDays(1)).mustRevalidate())
                .eTag(eTag)
                .body(list);
    }
}
