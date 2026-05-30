package com.example.task_manager_api.dto;

import com.example.task_manager_api.entity.TaskStatus;
import lombok.Data;

@Data
public class TaskUpdateDto {
    private String title;
    private String description;
    private TaskStatus status;
}
