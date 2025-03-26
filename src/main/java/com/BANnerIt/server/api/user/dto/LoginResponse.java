package com.BANnerIt.server.api.user.dto;

public record LoginResponse(
        String token,
        String message
) {}