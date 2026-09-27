package com.example.task_manager_api.controller;

import com.example.task_manager_api.dto.TaskResponseDto;
import com.example.task_manager_api.dto.UserResponseDto;
import com.example.task_manager_api.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(adminUserService.getAllUsers(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.getUserById(id));
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<Page<TaskResponseDto>> getTasksByUserId(
            @PathVariable Long id,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(adminUserService.getTasksByUserId(id, pageable));
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<String> blockUser(@PathVariable Long id){
        adminUserService.blockUser(id);
        return ResponseEntity.ok("User is blocked.");
    }

    @PutMapping("/{id}/unblock")
    public ResponseEntity<String> unblockUser(@PathVariable Long id){
        adminUserService.unblockUser(id);
        return ResponseEntity.ok("User is unblocked.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.ok("User successfully deleted.");
    }
}
