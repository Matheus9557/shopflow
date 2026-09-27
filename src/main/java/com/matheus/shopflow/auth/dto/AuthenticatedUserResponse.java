package com.matheus.shopflow.auth.dto;

public record AuthenticatedUserResponse(
        String email,
        String role
) {
}