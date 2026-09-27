package com.matheus.shopflow.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "User name cannot be empty")
        @Size(
                min = 2,
                max = 100,
                message = "User name must contain between 2 and 100 characters"
        )
        String name,

        @NotBlank(message = "User email cannot be empty")
        @Email(message = "User email must be valid")
        String email,

        @NotBlank(message = "User password cannot be empty")
        @Size(
                min = 8,
                max = 100,
                message = "User password must contain between 8 and 100 characters"
        )
        String password
) {
}