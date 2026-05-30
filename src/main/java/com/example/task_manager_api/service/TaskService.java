package com.example.task_manager_api.service;

import com.example.task_manager_api.dto.TaskRequestDto;
import com.example.task_manager_api.dto.TaskResponseDto;
import com.example.task_manager_api.dto.TaskUpdateDto;
import com.example.task_manager_api.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    void createTask(TaskRequestDto request);
    TaskResponseDto getTaskByTitle(String title);
    TaskResponseDto updateTask(Long id, TaskUpdateDto request);
    TaskResponseDto getTaskById(Long id);
    void deleteTask(Long id);
    Page<TaskResponseDto> getTasks(Pageable pageable);
    Page<TaskResponseDto> getTasksByStatus(TaskStatus status, Pageable pageable);

}
