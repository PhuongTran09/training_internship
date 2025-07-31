package com.example.backend.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.backend.dto.request.LoginRequest;
import com.example.backend.dto.response.TokenResponse;
import com.example.backend.service.auth.AuthService;

@Component("loginDelegate")
public class LoginCheckDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(LoginCheckDelegate.class);

    private final AuthService authService;

    public LoginCheckDelegate(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String username = (String) execution.getVariable("username");
        String password = (String) execution.getVariable("password");

        if (username == null || password == null) {
            throw new IllegalArgumentException("Thiếu thông tin đăng nhập");
        }

        log.info("Thực hiện kiểm tra đăng nhập cho user: {}", username);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        try {
            TokenResponse token = authService.login(loginRequest);

            execution.setVariable("loginSuccess", true);
            execution.setVariable("accessToken", token.getAccessToken());
            execution.setVariable("refreshToken", token.getRefreshToken());

            log.info("Đăng nhập thành công cho user: {}", username);
        } catch (RuntimeException e) {
            execution.setVariable("loginSuccess", false);
            execution.setVariable("errorMessage", e.getMessage());

            log.error("Đăng nhập thất bại: {}", e.getMessage());
        }
    }
}
