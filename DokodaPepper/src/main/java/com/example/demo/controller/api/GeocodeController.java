package com.example.demo.controller.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class GeocodeController {

    @Value("${my.api.key}")
    private String apiKey;

    @GetMapping("/geocode")
    public ResponseEntity<?> reverseGeocode(@RequestParam double lat, @RequestParam double lng) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&language=ja"
                + "&key=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();
        try {
            String result = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Geocode失敗: " + e.getMessage());
        }
    }

    @PostMapping("/geocode/address")
    public ResponseEntity<String> geocodeByAddress(@RequestBody Map<String, String> body) {
        String address = body.get("address");
        if (address == null || address.isBlank()) {
            return ResponseEntity.badRequest().body("住所が空です");
        }

        try {
            // なぜかエンコードしたURLはJavaからだとうまくいかない。ブラウザから直接検索する分には大丈夫だった。
//            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
            String url = String.format(
                    "https://maps.googleapis.com/maps/api/geocode/json?address=%s&region=jp&language=ja&key=%s",
                    address, apiKey);

            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(url, String.class);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("住所検索に失敗しました: " + e.getMessage());
        }
    }
}
