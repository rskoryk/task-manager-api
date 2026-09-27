package com.example.task_manager_api;

import com.example.task_manager_api.dto.UserResponseDto;
import com.example.task_manager_api.dto.UserUpdateDto;
import com.example.task_manager_api.entity.User;
import com.example.task_manager_api.exception.ResourceAlreadyExistsException;
import com.example.task_manager_api.repository.UserRepository;
import com.example.task_manager_api.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    private UserRepository userRepository;
    private UserServiceImpl userService;
    private PasswordEncoder passwordEncoder;
    private User user;

    @BeforeEach
    void setUp(){
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userRepository, passwordEncoder);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test12345");

        SecurityContextHolder.setContext(securityContext);

        user = new User();
        user.setId(1L);
        user.setUsername("test12345");
        user.setEmail("test12345@gmail.com");

        when(userRepository.findByUsername("test12345")).thenReturn(Optional.of(user));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testGetMe_successfullyReturnsUser(){
        UserResponseDto result = userService.getMe();

        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getRole(), result.getRole());
        assertEquals(user.getId(), result.getId());
    }

    @Test
    public void testDeleteMe_successfullyDeletesMe(){
        userService.deleteMe();

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testUpdateMe_successfullyUpdatesUser(){
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setUsername("newusername");

        when(userRepository.existsByUsername("newusername")).thenReturn(false);

        UserResponseDto result = userService.updateMe(updateDto);

        assertEquals("newusername", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateMe_throwsException_whenUsernameAlreadyExists(){
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setUsername("newusername");

        when(userRepository.existsByUsername("newusername")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.updateMe(userUpdateDto));
    }

    @Test
    public void testUpdateMe_throwsException_whenEmailAlreadyExists(){
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setEmail("test123@gmail.com");

        when(userRepository.existsByEmail("test123@gmail.com")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.updateMe(userUpdateDto));
    }
}
