package com.example.task_manager_api.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import com.example.task_manager_api.dto.TaskRequestDto;
import com.example.task_manager_api.dto.TaskResponseDto;
import com.example.task_manager_api.dto.TaskUpdateDto;
import com.example.task_manager_api.entity.Task;
import com.example.task_manager_api.entity.TaskStatus;
import com.example.task_manager_api.entity.User;
import com.example.task_manager_api.exception.ResourceAlreadyExistsException;
import com.example.task_manager_api.exception.ResourceNotFoundException;
import com.example.task_manager_api.mapper.TaskMapper;
import com.example.task_manager_api.repository.TaskRepository;
import com.example.task_manager_api.repository.UserRepository;
import com.example.task_manager_api.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private User getCurrentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public void createTask(TaskRequestDto request) {
        User owner = getCurrentUser();

        if (taskRepository.findByOwnerIdAndTitle(owner.getId(), request.getTitle()).isPresent()){
            throw new ResourceAlreadyExistsException(String.format("Task with title '%s' already exists", request.getTitle()));
        }

        try {
            Task task = TaskMapper.mapTaskRequestDtoToTask(request);
            task.setOwner(owner);
            taskRepository.save(task);
        } catch (DataIntegrityViolationException e){
            throw new ResourceAlreadyExistsException(String.format("Task with title '%s' already exists", request.getTitle()));
        }
    }

    @Override
    public TaskResponseDto getTaskByTitle(String title) {
        Task task = taskRepository.findByOwnerIdAndTitle(getCurrentUser().getId(), title).orElseThrow(() -> new ResourceNotFoundException(String.format("Task with title '%s' doesn't exist", title)));
        return TaskMapper.mapTaskToTaskResponseDto(task);
    }

    private Task getTaskAndVerifyOwnership(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(String.format("Task with id='%d' doesn't exist", id))
                );
        Long currentUserId = getCurrentUser().getId();
        if (!task.getOwner().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You don't have permission to get or update this task");
        }

        return task;
    }

    @Override
    public TaskResponseDto updateTask(Long id, TaskUpdateDto request) {
        Task task = getTaskAndVerifyOwnership(id);
        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());

        Task updatedTask = taskRepository.save(task);

        return TaskMapper.mapTaskToTaskResponseDto(updatedTask);
    }

    @Override
    public TaskResponseDto getTaskById(Long id) {
        Task task = getTaskAndVerifyOwnership(id);
        return TaskMapper.mapTaskToTaskResponseDto(task);
    }

    @Override
    public void deleteTask(Long id) {
        getTaskAndVerifyOwnership(id);
        taskRepository.deleteById(id);
    }

    @Override
    public Page<TaskResponseDto> getTasks(Pageable pageable) {
        Long userId = getCurrentUser().getId();
        return taskRepository.findAllByOwnerId(userId, pageable)
                .map(TaskMapper::mapTaskToTaskResponseDto);
    }

    @Override
    public Page<TaskResponseDto> getTasksByStatus(TaskStatus status, Pageable pageable) {
        Long userId = getCurrentUser().getId();
        return taskRepository.findAllByOwnerIdAndStatus(userId, status, pageable)
                .map(TaskMapper::mapTaskToTaskResponseDto);
    }
}
