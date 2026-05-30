package com.example.task_manager_api.controller;

import com.example.task_manager_api.dto.TaskRequestDto;
import com.example.task_manager_api.dto.TaskResponseDto;
import com.example.task_manager_api.dto.TaskUpdateDto;
import com.example.task_manager_api.entity.TaskStatus;
import com.example.task_manager_api.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<String> createTask(@Valid @RequestBody TaskRequestDto request) {
        taskService.createTask(request);
        return new ResponseEntity<>("Task successfully created!", HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id){
        TaskResponseDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/by-title/{title}")
    public ResponseEntity<TaskResponseDto> getTaskByTitle(@PathVariable String title) {
        TaskResponseDto task = taskService.getTaskByTitle(title);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateDto request) {
        TaskResponseDto taskDto = taskService.updateTask(id, request);
        return ResponseEntity.ok(taskDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok("Task successfully deleted!");
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponseDto>> getTasks(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
            ) {
        return ResponseEntity.ok(taskService.getTasks(pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<TaskResponseDto>> getTasksByStatus(
            @PathVariable TaskStatus status,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(taskService.getTasksByStatus(status, pageable));
    }
}
