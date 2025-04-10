package com.BANnerIt.server.api.user.dto;

import lombok.Builder;


@Builder
public record MemberUpdateRequest(
        String name,
        String userProfile
) { }
