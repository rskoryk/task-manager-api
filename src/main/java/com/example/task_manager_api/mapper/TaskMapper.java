package com.example.task_manager_api.mapper;

import com.example.task_manager_api.dto.TaskRequestDto;
import com.example.task_manager_api.dto.TaskResponseDto;
import com.example.task_manager_api.entity.Task;

public class TaskMapper {
    public static Task mapTaskRequestDtoToTask(TaskRequestDto request){
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        return task;
    }
    public static TaskResponseDto mapTaskToTaskResponseDto(Task task){
        TaskResponseDto response = new TaskResponseDto();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        return response;
    }
    public static Task mapTaskResponseDtoToTask(TaskResponseDto responseDto){
        Task task = new Task();
        task.setTitle(responseDto.getTitle());
        task.setDescription(responseDto.getDescription());
        task.setStatus(responseDto.getStatus());
        task.setCreatedAt(responseDto.getCreatedAt());
        task.setUpdatedAt(responseDto.getUpdatedAt());
        return task;
    }
}
