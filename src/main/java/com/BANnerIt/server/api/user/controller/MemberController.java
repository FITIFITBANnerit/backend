package com.BANnerIt.server.api.user.controller;

import com.BANnerIt.server.api.user.dto.MemberResponse;
import com.BANnerIt.server.api.user.dto.MemberUpdateRequest;
import com.BANnerIt.server.api.user.service.MemberService;
import com.BANnerIt.server.global.auth.JwtTokenUtil;
import com.BANnerIt.server.global.exception.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenUtil jwtTokenUtil;

    // 회원 정보 조회 (userId 기반)
    @GetMapping("/userdetail")
    public ResponseEntity<ApiResponse<MemberResponse>> getUserDetails(@RequestHeader("Authorization") String authorizationHeader) {
        Long userId = extractUserId(authorizationHeader);

        if (userId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("userId를 추출할 수 없습니다.", HttpStatus.BAD_REQUEST));
        }

        MemberResponse userResponse = memberService.getUserDetails(userId);
        return ResponseEntity.ok(ApiResponse.success(null, userResponse));
    }

    // 회원 정보 업데이트
    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<String>> updateUser(@Valid @RequestBody MemberUpdateRequest request,
                                                          @RequestHeader("Authorization") String authorizationHeader) {
        Long userId = extractUserId(authorizationHeader);

        if (userId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("userId를 추출할 수 없습니다.", HttpStatus.BAD_REQUEST));
        }

        memberService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success(null, "회원정보가 수정되었습니다."));
    }

    // 회원 탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteUser(@RequestHeader("Authorization") String authorizationHeader) {
        Long userId = extractUserId(authorizationHeader);

        if (userId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("userId를 추출할 수 없습니다.", HttpStatus.BAD_REQUEST));
        }

        memberService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "회원탈퇴가 완료되었습니다."));
    }

    private Long extractUserId(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        return jwtTokenUtil.extractUserId(token);
    }
}