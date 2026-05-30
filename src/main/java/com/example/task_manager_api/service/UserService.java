package com.example.task_manager_api.service;

import com.example.task_manager_api.dto.TaskResponseDto;
import com.example.task_manager_api.dto.UserResponseDto;
import com.example.task_manager_api.dto.UserUpdateDto;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponseDto update(Long id, UserUpdateDto dto);
    // ADMIN
    Page<UserResponseDto> getAllUsers(Pageable pageable);
    UserResponseDto getById(Long id);
    Page<TaskResponseDto> getTasksByUserId(Long userId, Pageable pageable);
    void toggleUserEnabled(Long id);
    void delete(Long id);
}
