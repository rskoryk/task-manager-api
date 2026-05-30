package com.example.task_manager_api;

import com.example.task_manager_api.dto.UserRequestDto;
import com.example.task_manager_api.entity.User;
import com.example.task_manager_api.exception.ResourceAlreadyExistsException;
import com.example.task_manager_api.exception.ResourceNotFoundException;
import com.example.task_manager_api.exception.TokenExpiredException;
import com.example.task_manager_api.repository.UserRepository;
import com.example.task_manager_api.security.JwtService;
import com.example.task_manager_api.service.impl.AuthServiceImpl;
import com.example.task_manager_api.service.impl.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@SpringBootTest
public class AuthServiceImplTest {

    private UserRepository userRepository;
    private AuthServiceImpl authService;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        authenticationManager = mock(AuthenticationManager.class);
        emailService = mock(EmailService.class);
        authService = new AuthServiceImpl(userRepository, passwordEncoder, jwtService, authenticationManager, emailService);

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    }

    @Test
    public void testRegistration_successfullyRegistersUser() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("testuser123");
        userRequestDto.setEmail("testuser123@gmail.com");
        userRequestDto.setPassword("test123");

        when(userRepository.existsByUsername("testuser123")).thenReturn(false);
        when(userRepository.existsByEmail("testuser123@gmail.com")).thenReturn(false);

        authService.register(userRequestDto);

        verify(emailService, times(1)).sendVerificationEmail(eq("testuser123@gmail.com"), any(String.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegistration_throwsException_whenUsernameAlreadyExists(){
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("testuser123");
        userRequestDto.setEmail("testuser123@gmail.com");
        userRequestDto.setPassword("test123");

        when(userRepository.existsByUsername("testuser123")).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class, () -> authService.register(userRequestDto));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testRegistration_throwsException_whenEmailAlreadyExists(){
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("testuser123");
        userRequestDto.setEmail("testuser123@gmail.com");
        userRequestDto.setPassword("test123");

        when(userRepository.existsByEmail("testuser123@gmail.com")).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class, () -> authService.register(userRequestDto));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testLogin_successfullyLoginsUser(){
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("testuser123");
        userRequestDto.setEmail("testuser123@gmail.com");
        userRequestDto.setPassword("test123");

        User user = new User();
        user.setUsername("testuser123");
        user.setEmail("testuser123@gmail.com");
        user.setPassword("test123");

        when(userRepository.findByUsername(userRequestDto.getUsername())).thenReturn(Optional.of(user));

        authService.login(userRequestDto);

        verify(jwtService, times(1)).generateToken(user.getUsername());
    }

    @Test
    public void testLogin_throwsException_whenUserNotFound(){
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("testuser123");
        userRequestDto.setEmail("testuser123@gmail.com");
        userRequestDto.setPassword("test123");

        when(userRepository.findByUsername(userRequestDto.getUsername())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.login(userRequestDto));
    }

    @Test
    public void testLogin_throwsException_whenPasswordIsWrong(){
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("testuser123");
        userRequestDto.setEmail("testuser123@gmail.com");
        userRequestDto.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(userRequestDto));
    }

    @Test
    public void testVerification_successfullyVerifiesToken(){
        String token = "token123";

        User user = new User();
        user.setUsername("testuser123");
        user.setEmail("testuser123@gmail.com");
        user.setPassword("wrongpassword");
        user.setVerificationTokenExpiration(LocalDateTime.now().plusHours(1));

        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.of(user));

        authService.verify(token);

        assertTrue(user.isEnabled());
        assertNull(user.getVerificationToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testVerification_throwsException_whenUserNotFoundByVerificationToken(){
        String token = "token123";

        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.verify(token));
    }

    @Test
    public void testVerification_throwsException_whenUserVerificationTokenIsExpired(){
        String token = "token123";

        User user = new User();
        user.setUsername("testuser123");
        user.setEmail("testuser123@gmail.com");
        user.setPassword("wrongpassword");
        user.setVerificationToken(token);
        user.setVerificationTokenExpiration(LocalDateTime.now().minusHours(1));

        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.of(user));

        assertThrows(TokenExpiredException.class, () -> authService.verify(token));

        verify(userRepository, never()).save(any(User.class));
    }
}
