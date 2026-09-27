package com.example.task_manager_api.service;

import com.example.task_manager_api.dto.UserResponseDto;
import com.example.task_manager_api.dto.UserUpdateDto;

public interface UserService {
    UserResponseDto getMe();
    UserResponseDto updateMe(UserUpdateDto dto);
    void deleteMe();
}
