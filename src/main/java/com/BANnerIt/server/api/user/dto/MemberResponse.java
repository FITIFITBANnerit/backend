package com.BANnerIt.server.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponse {
    private Long userId;
    private String email;
    private String name;
    private String role;
    private String userProfileUrl;
}