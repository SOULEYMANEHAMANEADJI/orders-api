package com.luqman.rest_api_orders.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Unhandled exception: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        long duration = System.currentTimeMillis() - start;
        log.info("{} {} {} {}ms", request.getMethod(), request.getRequestURI(), response.getStatus(), duration);
    }
}
