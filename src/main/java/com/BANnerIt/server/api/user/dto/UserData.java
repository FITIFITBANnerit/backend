package com.BANnerIt.server.api.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserData(

        @JsonProperty("name")
        String name,

        @JsonProperty("email")
        String email,

        @JsonProperty("profile_image_url")
        String profileImageUrl
) { }