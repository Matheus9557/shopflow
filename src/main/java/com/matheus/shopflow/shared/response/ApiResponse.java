package com.matheus.shopflow.shared.response;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data
) {}