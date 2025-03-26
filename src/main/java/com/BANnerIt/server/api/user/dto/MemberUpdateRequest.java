package com.BANnerIt.server.api.user.dto;

import lombok.Builder;

@Builder
public record MemberUpdateRequest(String mail, String updatedUser, String name, String userProfile, boolean b){

}