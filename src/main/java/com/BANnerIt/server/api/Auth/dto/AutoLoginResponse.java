package com.BANnerIt.server.api.Auth.dto;

import com.BANnerIt.server.api.user.dto.UserData;

public record AutoLoginResponse(String jwt, UserData userData) {}