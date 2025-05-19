package com.example.demo.config;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.demo.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

//    パスワードをプレーンテキストのまま比較するメソッド。模擬用
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new PasswordEncoder() {
//            @Override
//            public String encode(CharSequence rawPassword) {
//                return rawPassword.toString(); // エンコードせずそのまま返す
//            }
//
//            @Override
//            public boolean matches(CharSequence rawPassword, String encodedPassword) {
//                return rawPassword.toString().equals(encodedPassword); // プレーンで比較
//            }
//        };
//    }

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
        .cors(Customizer.withDefaults())
        .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/register", "/api/login", "/api/user")
                        .permitAll().anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .logout(logout -> logout.logoutUrl("/api/logout") // ログアウトエンドポイント
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // ログアウト成功時の処理（React側ログインページへリダイレクト）
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setHeader("Location", "http://localhost:3000/login");
                        }).invalidateHttpSession(true).deleteCookies("JSESSIONID").permitAll());

//        http
//        .cors(Customizer.withDefaults())
//        .csrf(csrf -> csrf.disable())
//        .authorizeHttpRequests(auth -> auth
//            .requestMatchers("/api/**", "/login", "/logout").permitAll() // APIは認証不要に
//            .anyRequest().authenticated())
//            .formLogin(form -> form
//                    .loginPage("/login") // 自作のログインフォームを使う場合（Thymeleafなど）
//                    .defaultSuccessUrl("http://localhost:3000", true)) // Reactに遷移させたい場合
//            .logout(logout -> logout
//                    .logoutUrl("/logout")       // POST /logout でログアウト
//                    .logoutSuccessUrl("/login") // ログアウト後のリダイレクト先
//                    .invalidateHttpSession(true)
//                    .deleteCookies("JSESSIONID")
//                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder()).and().build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE").allowedHeaders("*").allowCredentials(true);
            }
        };
    }


}
