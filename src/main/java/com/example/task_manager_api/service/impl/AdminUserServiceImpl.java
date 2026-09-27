package com.example.task_manager_api.service.impl;

import com.example.task_manager_api.dto.TaskResponseDto;
import com.example.task_manager_api.dto.UserResponseDto;
import com.example.task_manager_api.entity.User;
import com.example.task_manager_api.exception.ResourceNotFoundException;
import com.example.task_manager_api.mapper.TaskMapper;
import com.example.task_manager_api.mapper.UserMapper;
import com.example.task_manager_api.repository.TaskRepository;
import com.example.task_manager_api.repository.UserRepository;
import com.example.task_manager_api.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    private User findUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User with id=%d doesn't exist", id)));
    }

    @Override
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserMapper::mapUserToUserResponseDto);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = findUserById(id);
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
    public void blockUser(Long id) {
        User user = findUserById(id);
        user.setBlocked(true);
        userRepository.save(user);
    }

    @Override
    public void unblockUser(Long id) {
        User user = findUserById(id);
        user.setBlocked(false);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    String.format("User with id='%d' doesn't exist", id));
        }
        userRepository.deleteById(id);
    }
}
