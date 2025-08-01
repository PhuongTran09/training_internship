package com.example.backend.service.task;

import org.camunda.bpm.engine.task.Task;

import java.util.List;
import java.util.Map;

public interface IProcessService {
    void completeTask(String taskId, Map<String, Object> variables);
    Map<String, Object> getVariables(String taskId);
    List<Task> getAllTasks();
//    String claimTask(String taskId);
}
