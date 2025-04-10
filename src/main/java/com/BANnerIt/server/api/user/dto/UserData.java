package com.BANnerIt.server.api.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserData(

        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("role")
        String role,

        @JsonProperty("name")
        String name,

        @JsonProperty("email")
        String email,

        @JsonProperty("profile_image_url")
        String userProfileUrl
) { }
