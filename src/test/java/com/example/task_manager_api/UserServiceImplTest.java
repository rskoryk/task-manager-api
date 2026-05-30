package com.example.task_manager_api;

import com.example.task_manager_api.dto.TaskResponseDto;
import com.example.task_manager_api.dto.UserResponseDto;
import com.example.task_manager_api.dto.UserUpdateDto;
import com.example.task_manager_api.entity.Task;
import com.example.task_manager_api.entity.TaskStatus;
import com.example.task_manager_api.entity.User;
import com.example.task_manager_api.exception.ResourceAlreadyExistsException;
import com.example.task_manager_api.exception.ResourceNotFoundException;
import com.example.task_manager_api.mapper.UserMapper;
import com.example.task_manager_api.repository.TaskRepository;
import com.example.task_manager_api.repository.UserRepository;
import com.example.task_manager_api.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceImplTest {

    private UserRepository userRepository;
    private TaskRepository taskRepository;
    private UserServiceImpl userService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp(){
        userRepository = mock(UserRepository.class);
        taskRepository = mock(TaskRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userRepository, taskRepository, passwordEncoder);
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

        Page<UserResponseDto> result = userService.getAllUsers(Pageable.unpaged());

        assertEquals("test12345", result.getContent().getFirst().getUsername());
    }

    @Test
    public void testGetById_successfullyReturnsUser(){
        Long userId = 1L;

        UserResponseDto userResponseDto1 = new UserResponseDto();
        userResponseDto1.setId(userId);
        userResponseDto1.setUsername("test12345");
        userResponseDto1.setEmail("test12345@gmail.com");

        User user = UserMapper.mapUserResponseDtoToUser(userResponseDto1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponseDto userResponseDto2 = userService.getById(userId);

        assertEquals(userResponseDto1, userResponseDto2);
    }

    @Test
    public void testGetById_throwsException_whenUserNotFound(){
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getById(userId));
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

        Page<TaskResponseDto> result = userService.getTasksByUserId(1L, Pageable.unpaged());

        assertEquals(1L, result.getTotalElements());
        assertEquals("Task-1", result.getContent().getFirst().getTitle());
    }

    @Test
    public void testGetTasksByUserId_throwsException_whenUserNotFound(){
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.getTasksByUserId(userId, Pageable.unpaged()));
    }

    @Test
    public void testToggleUserEnabled_successfullyTogglesUser(){
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setEnabled(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.toggleUserEnabled(userId);

        assertTrue(user.isEnabled());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testToggleUserEnabled_throwsException_whenUserNotFound(){
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.toggleUserEnabled(userId));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDelete_successfullyDeletesUser(){
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);

        userService.delete(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void testDelete_throwsException_whenUserNotFound(){
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.delete(userId));
    }

    @Test
    public void testUpdate_successfullyUpdatesUser(){
        Long userId = 1L;

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setUsername("newusername");

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(userRepository.existsByUsername("newusername")).thenReturn(false);

        UserResponseDto result = userService.update(userId, updateDto);

        assertEquals("newusername", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdate_throwsException_whenUsernameAlreadyExists(){
        Long userId = 1L;
        String username = "test123";

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setUsername(username);

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(userRepository.existsByUsername(username)).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.update(userId, userUpdateDto));
    }

    @Test
    public void testUpdate_throwsException_whenEmailAlreadyExists(){
        Long userId = 1L;
        String email = "test123@gmail.com";

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setEmail(email);

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.update(userId, userUpdateDto));
    }
}
