package com.example.task_manager_api.service.impl;

import com.example.task_manager_api.dto.TaskResponseDto;
import com.example.task_manager_api.dto.UserRequestDto;
import com.example.task_manager_api.dto.UserResponseDto;
import com.example.task_manager_api.dto.UserUpdateDto;
import com.example.task_manager_api.entity.User;
import com.example.task_manager_api.exception.ResourceAlreadyExistsException;
import com.example.task_manager_api.exception.ResourceNotFoundException;
import com.example.task_manager_api.mapper.TaskMapper;
import com.example.task_manager_api.mapper.UserMapper;
import com.example.task_manager_api.repository.TaskRepository;
import com.example.task_manager_api.repository.UserRepository;
import com.example.task_manager_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto update(Long id, UserUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User with id=%d doesn't exist", id)));
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
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserMapper::mapUserToUserResponseDto);
    }

    @Override
    public UserResponseDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User with id='%d' doesn't exist", id)));
        return UserMapper.mapUserToUserResponseDto(user);
    }

    @Override
    public Page<TaskResponseDto> getTasksByUserId(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    String.format("User with id='%d' doesn't exist", userId));
        }
        return taskRepository.findAllByOwnerId(userId, pageable)
                .map(TaskMapper::mapTaskToTaskResponseDto);
    }

    @Override
    public void toggleUserEnabled(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User with id='%d' doesn't exist", id)));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    String.format("User with id='%d' doesn't exist", id));
        }
        userRepository.deleteById(id);
    }
}
