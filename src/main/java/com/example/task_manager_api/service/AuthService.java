package com.example.task_manager_api.service;

import com.example.task_manager_api.dto.UserRequestDto;

public interface AuthService {
    String register(UserRequestDto request);
    String login(UserRequestDto request);
    String verify(String token);
}
