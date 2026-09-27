package com.example.task_manager_api;

import com.example.task_manager_api.dto.TaskResponseDto;
import com.example.task_manager_api.dto.UserResponseDto;
import com.example.task_manager_api.entity.Task;
import com.example.task_manager_api.entity.TaskStatus;
import com.example.task_manager_api.entity.User;
import com.example.task_manager_api.exception.ResourceNotFoundException;
import com.example.task_manager_api.mapper.UserMapper;
import com.example.task_manager_api.repository.TaskRepository;
import com.example.task_manager_api.repository.UserRepository;
import com.example.task_manager_api.service.impl.AdminUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AdminUserServiceImplTest {

    private UserRepository userRepository;
    private TaskRepository taskRepository;
    private AdminUserServiceImpl adminUserService;

    @BeforeEach
    void setUp(){
        userRepository = mock(UserRepository.class);
        taskRepository = mock(TaskRepository.class);
        adminUserService = new AdminUserServiceImpl(userRepository, taskRepository);
    }

    @Test
    public void testGetAllUsers_successfullyReturnsPageOfUsers(){
        User user = new User();
        user.setUsername("test12345");
        user.setEmail("test12345@gmail.com");
        user.setPassword("test12345");

        List<User> users = List.of(user);
        Page<User> page = new PageImpl<>(users);

        when(userRepository.findAll(Pageable.unpaged())).thenReturn(page);

        Page<UserResponseDto> result = adminUserService.getAllUsers(Pageable.unpaged());

        assertEquals("test12345", result.getContent().getFirst().getUsername());
    }

    @Test
    public void testGetUserById_successfullyReturnsUser(){
        Long userId = 1L;

        UserResponseDto userResponseDto1 = new UserResponseDto();
        userResponseDto1.setId(userId);
        userResponseDto1.setUsername("test12345");
        userResponseDto1.setEmail("test12345@gmail.com");

        User user = UserMapper.mapUserResponseDtoToUser(userResponseDto1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponseDto userResponseDto2 = adminUserService.getUserById(userId);

        assertEquals(userResponseDto1, userResponseDto2);
    }

    @Test
    public void testGetUserById_throwsException_whenUserNotFound(){
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminUserService.getUserById(userId));
    }

    @Test
    public void testGetTasksByUserId_successfullyReturnsPage(){
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task-1");
        task1.setDescription("desc");
        task1.setStatus(TaskStatus.TO_DO);

        List<Task> tasks = List.of(task1);
        Page<Task> page = new PageImpl<>(tasks);

        when(userRepository.existsById(1L)).thenReturn(true);

        when(taskRepository.findAllByOwnerId(1L, Pageable.unpaged())).thenReturn(page);

        Page<TaskResponseDto> result = adminUserService.getTasksByUserId(1L, Pageable.unpaged());

        assertEquals(1L, result.getTotalElements());
        assertEquals("Task-1", result.getContent().getFirst().getTitle());
    }

    @Test
    public void testGetTasksByUserId_throwsException_whenUserNotFound(){
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> adminUserService.getTasksByUserId(userId, Pageable.unpaged()));
    }

    @Test
    public void testBlockUser_successfullyBlocksUser(){
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setBlocked(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        adminUserService.blockUser(userId);

        assertTrue(user.isBlocked());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUnblockUser_successfullyUnblocksUser(){
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setBlocked(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        adminUserService.unblockUser(userId);

        assertFalse(user.isBlocked());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUnblockUser_throwsException_whenUserNotFound(){
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminUserService.unblockUser(userId));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testBlockUser_throwsException_whenUserNotFound(){
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminUserService.blockUser(userId));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDelete_successfullyDeletesUser(){
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);

        adminUserService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void testDelete_throwsException_whenUserNotFound(){
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> adminUserService.deleteUser(userId));
    }
}
