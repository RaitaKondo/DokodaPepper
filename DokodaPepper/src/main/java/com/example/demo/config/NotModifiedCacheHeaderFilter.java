package com.example.demo.config;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class NotModifiedCacheHeaderFilter extends OncePerRequestFilter {

    private static final String CACHE_HEADER_VALUE = "max-age=86400, must-revalidate";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        filterChain.doFilter(request, response);

        // 304 が返されるレスポンスなら Cache-Control を再設定
        if (response.getStatus() == HttpServletResponse.SC_NOT_MODIFIED) {
            response.setHeader(HttpHeaders.CACHE_CONTROL, CACHE_HEADER_VALUE);
        }
    }
}
