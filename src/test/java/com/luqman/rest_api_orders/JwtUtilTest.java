package com.luqman.rest_api_orders;

import com.luqman.rest_api_orders.config.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtUtil Tests")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(
                "404d6c756f2f4c756f2f4c756f000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20",
                86400000
        );
    }

    @Test
    @DisplayName("Should generate and validate a valid token")
    void testGenerateAndValidateToken() {
        String token = jwtUtil.generateToken("testuser", "USER");

        assertNotNull(token);
        assertTrue(jwtUtil.isTokenValid(token));
        assertEquals("testuser", jwtUtil.extractUsername(token));
    }

    @Test
    @DisplayName("Should return false for invalid token")
    void testInvalidToken() {
        assertFalse(jwtUtil.isTokenValid("invalid.token.here"));
    }

    @Test
    @DisplayName("Should return false for empty token")
    void testEmptyToken() {
        assertFalse(jwtUtil.isTokenValid(""));
    }

    @Test
    @DisplayName("Should extract username from generated token")
    void testExtractUsername() {
        String token = jwtUtil.generateToken("admin", "ADMIN");
        assertEquals("admin", jwtUtil.extractUsername(token));
    }
}
