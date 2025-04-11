package com.BANnerIt.server.api.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;


@Builder
public record MemberUpdateRequest(
        String name,
        @JsonProperty("user_profile_url") String userProfile
) { }
