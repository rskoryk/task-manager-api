package com.example.task_manager_api.service;

import com.example.task_manager_api.dto.TaskResponseDto;
import com.example.task_manager_api.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminUserService {
    Page<UserResponseDto> getAllUsers(Pageable pageable);
    UserResponseDto getUserById(Long id);
    Page<TaskResponseDto> getTasksByUserId(Long userId, Pageable pageable);
    void blockUser(Long id);
    void unblockUser(Long id);
    void deleteUser(Long id);
}
