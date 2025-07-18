package com.example.backend.service.auth;

import com.example.backend.dto.request.LoginRequest;
import com.example.backend.dto.request.RegisterRequest;
import com.example.backend.dto.response.TokenResponse;
import com.example.backend.entity.User;

public interface AuthService {
    TokenResponse login(LoginRequest request);
    User register(RegisterRequest registerUser);

}
