package com.example.task_manager_api.mapper;

import com.example.task_manager_api.dto.UserResponseDto;
import com.example.task_manager_api.entity.User;

public class UserMapper {
    public static UserResponseDto mapUserToUserResponseDto(User user){
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setEnabled(user.isEnabled());
        return dto;
    }

    public static User mapUserResponseDtoToUser(UserResponseDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        return user;
    }
}
