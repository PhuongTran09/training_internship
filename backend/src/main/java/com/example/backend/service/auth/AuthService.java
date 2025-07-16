package com.example.backend.service.auth;

import com.example.backend.dto.request.LoginRequest;
import com.example.backend.dto.response.TokenResponse;

public interface AuthService {
    TokenResponse login(LoginRequest request);
    
}
