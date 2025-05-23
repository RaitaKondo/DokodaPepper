package com.example.demo.controller.api;

import java.time.Duration;
import java.util.List;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.form.CityForm;
import com.example.demo.service.CityService;

@RestController
public class LocationController {

    private final CityService cityService;

    public LocationController(CityService cityService) {
        this.cityService = cityService;
    }

    /**
     * /api/prefectures で都道府県一覧を取得したあと、
     * React側で選択されたprefIdを指定して市区町村をロード
     */
    
    // 都道府県のIDを指定して市区町村を取得するエンドポイント。数が多すぎるのでETagやmustRevalidateは付与せず、TTL超過後は必ずフレッシュなデータを渡す。
    @GetMapping("/api/prefectures/{prefId}/cities")
    public ResponseEntity<List<CityForm>> getCitiesByPref(
            @PathVariable Integer prefId) {
        List<CityForm> cities = cityService.fetchByPrefecture(prefId);
        // こういった動的エンドポイントにもキャッシュをかけたい場合は
        // Cache-Control ヘッダーを同様に付与できます
        return ResponseEntity
            .ok()
            .cacheControl(CacheControl.maxAge(Duration.ofHours(6)))
            .body(cities);
    }
}
