package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .cors(Customizer.withDefaults())
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/**", "/login", "/logout").permitAll() // APIは認証不要に
            .anyRequest().authenticated())
            .formLogin(form -> form
                    .loginPage("/login") // 自作のログインフォームを使う場合（Thymeleafなど）
                    .defaultSuccessUrl("http://localhost:3000", true)) // Reactに遷移させたい場合
            .logout(logout -> logout
                    .logoutUrl("/logout")       // POST /logout でログアウト
                    .logoutSuccessUrl("/login") // ログアウト後のリダイレクト先
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                );

    return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:3000").allowCredentials(true)
                        .allowedMethods("*");
            }
        };
    }

}
