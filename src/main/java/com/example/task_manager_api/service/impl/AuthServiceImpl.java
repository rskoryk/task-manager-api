package com.example.task_manager_api.service.impl;

import com.example.task_manager_api.dto.UserRequestDto;
import com.example.task_manager_api.entity.Role;
import com.example.task_manager_api.entity.User;
import com.example.task_manager_api.exception.ResourceAlreadyExistsException;
import com.example.task_manager_api.exception.ResourceNotFoundException;
import com.example.task_manager_api.exception.TokenExpiredException;
import com.example.task_manager_api.repository.UserRepository;
import com.example.task_manager_api.security.JwtService;
import com.example.task_manager_api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Override
    public String register(UserRequestDto request) {
        if (userRepository.existsByUsername(request.getUsername())){
            throw new ResourceAlreadyExistsException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())){
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        String verificationToken = UUID.randomUUID().toString();

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setEnabled(false);
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiration(LocalDateTime.now().plusHours(24));

        userRepository.save(user);

        emailService.sendVerificationEmail(request.getEmail(), verificationToken);


        return "Registration successful! Please check your email to verify your account.";
    }

    @Override
    public String login(UserRequestDto request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return jwtService.generateToken(user.getUsername());
    }

    @Override
    public String verify(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid verification token"));

        if (user.getVerificationTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Verification token expired");
        }

        user.setEnabled(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiration(null);

        userRepository.save(user);

        return "Email successfully verified! You can now log in.";
    }
}
