package com.BANnerIt.server.api.user.dto;

public record AutoLoginResponse(String jwt, UserData userData) {}