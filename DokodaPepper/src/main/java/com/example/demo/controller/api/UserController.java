package com.example.demo.controller.api;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.form.UserForm;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserForm request, HttpSession session) {
        boolean success = userService.registerUser(request.getUsername(), request.getPassword());

        if (!success) {
            return ResponseEntity.badRequest().body("ユーザー名がすでに存在します");
        }

        // 認証トークンを作成（登録成功後の自動ログイン）
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            request.getUsername(),
            request.getPassword()
        );

        // 実際に認証を実行（パスワード照合）
        Authentication authenticated = authenticationManager.authenticate(authentication);

        // セキュリティコンテキストにセット（Spring Securityが管理）
        SecurityContextHolder.getContext().setAuthentication(authenticated);

        // セッションにもセット（ブラウザセッションでログイン保持）
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return ResponseEntity.ok("登録と自動ログイン成功");
    }
    
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserForm request, HttpSession session) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );

            System.out.println(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            System.out.println("ログイン成功");

            return ResponseEntity.ok("ログイン成功");

        } catch (AuthenticationException e) {
            System.out.println("ログイン失敗");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("認証失敗");
        }
    }


}
