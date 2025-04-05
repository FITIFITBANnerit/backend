package com.BANnerIt.server.api.user.dto;

public record MemberResponse(
        Long userId,
        String email,
        String name,
        String role,
        String userProfileUrl
) {
}