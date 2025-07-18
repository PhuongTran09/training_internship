package com.example.backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/process")
public class ProcessConTroller {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;


    @PostMapping("/start")
    public String startProcess(@RequestParam(required = false) String businessKey) {
        if (businessKey == null || businessKey.isEmpty()) {
            businessKey = UUID.randomUUID().toString();
        }

        runtimeService.startProcessInstanceByKey("Process_08kipkg", businessKey);
        return "Process started with key: " + businessKey;

    }

    @GetMapping("/tasks")
    public List<Map<String, Object>> getTasks() {
        List<Task> tasks = taskService.createTaskQuery().active().list();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Task task : tasks) {
            Map<String, Object> taskInfo = new HashMap<>();
            taskInfo.put("id", task.getId());
            taskInfo.put("name", task.getName());
            taskInfo.put("assignee", task.getAssignee());
            taskInfo.put("processInstanceId", task.getProcessInstanceId());
            result.add(taskInfo);
        }
        return result;
    }

    @PostMapping("/complete")
    public String completeTask(@RequestParam String taskId, @RequestParam boolean approved) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", approved);
        taskService.complete(taskId, variables);
        return "Task completed. Approved: " + approved;
    }

    

}
