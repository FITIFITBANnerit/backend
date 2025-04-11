package com.BANnerIt.server.api.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MemberResponse(
        Long id,
        String role,
        String email,
        String name,
        @JsonProperty("user_profile_url") String userProfileUrl
) { }
