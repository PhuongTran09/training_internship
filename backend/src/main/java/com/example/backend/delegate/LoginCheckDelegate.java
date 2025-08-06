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
            throw new IllegalArgumentException("❌ Thiếu thông tin đăng nhập");
        }

        log.info("🔐 Kiểm tra đăng nhập cho user: {}", username);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        try {
            TokenResponse token = authService.login(loginRequest);

            // Đăng nhập thành công
            execution.setVariable("loginSuccess", Boolean.TRUE); // 🔑 Phải là Boolean TRUE
            execution.setVariable("accessToken", token.getAccessToken());
            execution.setVariable("refreshToken", token.getRefreshToken());
            execution.setVariable("attemptCount", 0); // Reset nếu thành công

            log.info("Đăng nhập thành công cho user: {}", username);

        } catch (RuntimeException e) {
            // Đăng nhập thất bại
            execution.setVariable("loginSuccess", Boolean.FALSE); // 🔑 Phải là Boolean FALSE

            Integer currentAttempt = (Integer) execution.getVariable("attemptCount");
            if (currentAttempt == null) currentAttempt = 0;
            execution.setVariable("attemptCount", currentAttempt + 1);
            execution.setVariable("errorMessage", e.getMessage());

            log.warn(" Đăng nhập thất bại cho user {}: {}", username, e.getMessage());
        }

        // Log biến cuối cùng để debug BPMN flow
        log.info("🔎 loginSuccess: {}", execution.getVariable("loginSuccess"));
        log.info("🔎 attemptCount: {}", execution.getVariable("attemptCount"));
    }
}
