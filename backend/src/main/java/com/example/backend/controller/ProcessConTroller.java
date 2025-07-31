package com.example.backend.controller;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import java.util.*;

@RestController
@RequestMapping("/api/process")
public class ProcessConTroller {

    private static final Logger log = LoggerFactory.getLogger(ProcessConTroller.class);

    private final RuntimeService runtimeService;
    private final TaskService taskService;

    public ProcessConTroller(RuntimeService runtimeService, TaskService taskService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    @GetMapping("/start")
    public ResponseEntity<String> startProcess(
            @RequestParam(required = false) String businessKey,
            @RequestParam String username,
            @RequestParam String password
    ) {
        try {
            if (username == null || username.trim().isEmpty() ||
                    password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Thiếu username hoặc password");
            }

            if (businessKey == null || businessKey.trim().isEmpty()) {
                businessKey = UUID.randomUUID().toString();
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("username", username);
            variables.put("password", password);

            runtimeService.startProcessInstanceByKey("Process_Login", businessKey, variables);
            log.info("Started Process_Login with businessKey={}, username={}", businessKey, username);
            return ResponseEntity.ok("Process started with key: " + businessKey);

        } catch (Exception e) {
            log.error("Error starting process", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống. Vui lòng thử lại sau.");
        }
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<Map<String, Object>>> getTasks() {
        try {
            List<Task> tasks = taskService.createTaskQuery().active().list();
            List<Map<String, Object>> result = new ArrayList<>();

            for (Task task : tasks) {
                Map<String, Object> taskInfo = new HashMap<>();
                taskInfo.put("id", task.getId());
                taskInfo.put("name", task.getName());
                taskInfo.put("assignee", task.getAssignee());
                taskInfo.put("processInstanceId", task.getProcessInstanceId());
                taskInfo.put("variables", taskService.getVariables(task.getId()));
                result.add(taskInfo);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error fetching tasks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @PostMapping("/tasks/{taskId}/complete")
    public ResponseEntity<String> completeTask(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> variables
    ) {
        try {
            taskService.complete(taskId, new HashMap<>(variables));
            log.info("Task {} completed with variables: {}", taskId, variables);
            return ResponseEntity.ok("Task completed: " + taskId);
        } catch (Exception e) {
            log.error("Error completing task {}", taskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi xử lý tác vụ. Vui lòng thử lại.");
        }
    }
}
