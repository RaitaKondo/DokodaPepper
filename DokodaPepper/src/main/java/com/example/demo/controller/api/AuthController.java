package com.example.demo.controller.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.repository.CityRepository;
import com.example.demo.repository.PrefectureRepository;

@RestController
public class AuthController {
    private PrefectureRepository prefectureRepository;
    private CityRepository cityRepository;

    AuthController() {
        this.prefectureRepository = prefectureRepository;
        this.cityRepository = cityRepository;
    }

    @GetMapping("/api/user")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未ログイン");
        }

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("username", authentication.getName());

        return ResponseEntity.ok(userInfo);
    }

}
