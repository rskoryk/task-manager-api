package com.example.task_manager_api.service.impl;

import com.example.task_manager_api.dto.UserResponseDto;
import com.example.task_manager_api.dto.UserUpdateDto;
import com.example.task_manager_api.entity.User;
import com.example.task_manager_api.exception.ResourceAlreadyExistsException;
import com.example.task_manager_api.exception.ResourceNotFoundException;
import com.example.task_manager_api.mapper.UserMapper;
import com.example.task_manager_api.repository.UserRepository;
import com.example.task_manager_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private User getCurrentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );
    }

    @Override
    public UserResponseDto getMe() {
        User user = getCurrentUser();
        return UserMapper.mapUserToUserResponseDto(user);
    }

    @Override
    public UserResponseDto updateMe(UserUpdateDto dto) {
        User user = getCurrentUser();
        if (dto.getUsername() != null){
            if (userRepository.existsByUsername(dto.getUsername())) {
                throw new ResourceAlreadyExistsException("Username already exists");
            }
            user.setUsername(dto.getUsername());
        }
        if (dto.getEmail() != null) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new ResourceAlreadyExistsException("Email already exists");
            }
            user.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null) user.setPassword(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);
        return UserMapper.mapUserToUserResponseDto(user);
    }

    @Override
    public void deleteMe() {
        User user = getCurrentUser();
        userRepository.delete(user);
    }
}
