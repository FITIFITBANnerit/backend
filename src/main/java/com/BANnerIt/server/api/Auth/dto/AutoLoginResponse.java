package com.BANnerIt.server.api.Auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AutoLoginResponse(
        String accessToken,

        @JsonProperty("user_data")
        UserData userData
) {}
