package com.example.task_manager_api.controller;

import com.example.task_manager_api.dto.UserResponseDto;
import com.example.task_manager_api.dto.UserUpdateDto;
import com.example.task_manager_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponseDto> getMe() {
        return ResponseEntity.ok(userService.getMe());
    }

    @PutMapping
    public ResponseEntity<UserResponseDto> updateMe(@Valid @RequestBody UserUpdateDto request) {
        return ResponseEntity.ok(userService.updateMe(request));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMe() {
        userService.deleteMe();
        return ResponseEntity.noContent().build();
    }
}
