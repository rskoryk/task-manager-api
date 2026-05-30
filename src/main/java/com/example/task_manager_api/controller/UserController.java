package com.example.task_manager_api.controller;

import com.example.task_manager_api.dto.UserResponseDto;
import com.example.task_manager_api.dto.UserUpdateDto;
import com.example.task_manager_api.entity.User;
import com.example.task_manager_api.exception.ResourceNotFoundException;
import com.example.task_manager_api.mapper.UserMapper;
import com.example.task_manager_api.repository.UserRepository;
import com.example.task_manager_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public User getCurrentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @GetMapping
    public ResponseEntity<UserResponseDto> getMe() {
        User user = getCurrentUser();
        return ResponseEntity.ok(UserMapper.mapUserToUserResponseDto(user));
    }

    @PutMapping
    public ResponseEntity<UserResponseDto> updateMe(@Valid @RequestBody UserUpdateDto request) {
        User user = getCurrentUser();
        return ResponseEntity.ok(userService.update(user.getId(), request));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteMe() {
        User user = getCurrentUser();
        userService.delete(user.getId());
        return ResponseEntity.ok("Account successfully deleted");
    }
}
