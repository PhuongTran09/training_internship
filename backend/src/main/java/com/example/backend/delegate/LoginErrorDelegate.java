package com.example.backend.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("loginErrorDelegate")
public class LoginErrorDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(LoginErrorDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        Integer retryCount = (Integer) execution.getVariable("retryCount");
        if (retryCount == null) retryCount = 0;

        retryCount++;
        execution.setVariable("retryCount", retryCount);

        // Chỉ set error message mặc định nếu chưa có
        if (execution.getVariable("errorMessage") == null) {
            execution.setVariable("errorMessage", "Tài khoản hoặc mật khẩu không đúng");
        }

        String username = (String) execution.getVariable("username");

        if (retryCount < 3) {
            execution.setVariable("tryAgain", true);
            log.warn("User '{}' đăng nhập sai lần thứ {}. Cho phép thử lại.", username, retryCount);
        } else {
            execution.setVariable("tryAgain", false);
            log.error("User '{}' đã vượt quá số lần đăng nhập cho phép.", username);
        }
    }
}
