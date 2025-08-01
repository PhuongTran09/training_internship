package com.example.backend.service.task;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProcessService implements IProcessService {

    private final TaskService taskService;

    public ProcessService(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public List<Task> getAllTasks() {
        return taskService.createTaskQuery().active().list();
    }

    @Override
    public Map<String, Object> getVariables(String taskId) {
        return taskService.getVariables(taskId);
    }

    @Override
    public void completeTask(String taskId, Map<String, Object> variables) {
        taskService.complete(taskId, variables);
    }
//    @Override
//   public String claimTask(String taskId) {
//        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
//
//        if (task == null) {
//            return "Task không tồn tại.";
//        }
//
//        if (task.getAssignee() != null) {
//            return "Task đã được claim bởi: " + task.getAssignee();
//        }
//
//        // Lấy username từ Spring Security
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        taskService.claim(taskId, username);
//        return "Claimed bởi user: " + username;
//    }
}
