package com.example.ecommerce.security;

public class AuthResponse {
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    // Getter et setter
    public String getToken() {
        return token;
    }

}