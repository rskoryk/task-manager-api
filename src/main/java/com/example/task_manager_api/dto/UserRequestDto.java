package com.example.task_manager_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDto {

    @NotBlank
    @Size(min = 5, max = 30)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Only letters, numbers and \"_\" are allowed")
    private String username;

    @NotBlank
    @Email(message = "Invalid email format")
    @Size(max = 254, message = "Email is too long")
    private String email;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;
}