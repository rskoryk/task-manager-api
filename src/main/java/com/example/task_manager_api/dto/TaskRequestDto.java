package com.example.task_manager_api.dto;

import com.example.task_manager_api.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskRequestDto {

    @NotBlank
    @Size(min = 3, max = 64, message = "Title must be 3-64 characters")
    private String title;
    @NotBlank
    @Size(min = 5, max = 1000, message = "Description must be 5-1000 characters")
    private String description;
    private TaskStatus status = TaskStatus.TO_DO;
}
