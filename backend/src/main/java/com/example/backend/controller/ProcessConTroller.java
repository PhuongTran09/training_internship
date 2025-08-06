package com.example.backend.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.service.task.IProcessService;

@RestController
@RequestMapping("/api/process")
public class ProcessConTroller {

    private static final Logger log = LoggerFactory.getLogger(ProcessConTroller.class);

    private final RuntimeService runtimeService;
    private final IProcessService processService;

    public ProcessConTroller(RuntimeService runtimeService, IProcessService processService) {
        this.runtimeService = runtimeService;
        this.processService = processService;
    }

    @GetMapping("/start")
    public ResponseEntity<Map<String, Object>> startProcess(
            @RequestParam(required = false) String businessKey,
            @RequestParam String username,
            @RequestParam String password
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (username.isBlank() || password.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thiếu username hoặc password"));
            }

            if (businessKey == null || businessKey.isBlank()) {
                businessKey = UUID.randomUUID().toString();
            }

            Map<String, Object> variables = Map.of(
                    "username", username,
                    "password", password
            );

            runtimeService.startProcessInstanceByKey("Process_Login", businessKey, variables);
            log.info("Started Process_Login with businessKey={}, username={}", businessKey, username);

            response.put("message", "Process started");
            response.put("businessKey", businessKey);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error starting process", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi hệ thống. Vui lòng thử lại sau."));
        }
    }


    @GetMapping("/tasks")
    public ResponseEntity<List<Map<String, Object>>> getTasks() {
        try {
            List<Task> tasks = processService.getAllTasks();
            List<Map<String, Object>> result = new ArrayList<>();

            for (Task task : tasks) {
                Map<String, Object> taskInfo = new HashMap<>();
                taskInfo.put("id", task.getId());
                taskInfo.put("name", task.getName());
                taskInfo.put("assignee", task.getAssignee());
                taskInfo.put("processInstanceId", task.getProcessInstanceId());
                taskInfo.put("variables", processService.getVariables(task.getId()));
                result.add(taskInfo);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error fetching tasks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @PostMapping("/tasks/{taskId}/complete")
    public ResponseEntity<Map<String, Object>> completeTask(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> variables
    ) {
        try {
            processService.completeTask(taskId, variables);
            log.info("Task {} completed with variables: {}", taskId, variables);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Task completed successfully",
                    "taskId", taskId
            ));

        } catch (Exception e) {
            log.error("Error completing task {}", taskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Failed to complete task",
                    "error", e.getMessage()
            ));
        }
    }
//    @PostMapping("/tasks/{taskId}/claim")
//    public ResponseEntity<?> claimTask(@PathVariable String taskId) {
//        String result = processService.claimTask(taskId);
//        return ResponseEntity.ok(Collections.singletonMap("message", result));
//    }
}

