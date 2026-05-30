package com.example.task_manager_api.dto;

import com.example.task_manager_api.entity.Role;
import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private boolean enabled;
}
