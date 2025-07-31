package com.example.backend.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("loginSuccessDelegate")
public class LoginSuccessDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(LoginSuccessDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        String accessToken = (String) execution.getVariable("accessToken");
        String refreshToken = (String) execution.getVariable("refreshToken");
        String username = (String) execution.getVariable("username");

        log.info("User '{}' đăng nhập thành công.", username);

        execution.setVariable("success", "LOGIN_SUCCESS");
    }
}
