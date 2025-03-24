package com.BANnerIt.server.api.user.controller;

import com.BANnerIt.server.api.user.dto.MemberResponse;
import com.BANnerIt.server.api.user.dto.MemberSignUpRequest;
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

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> registerUser(@RequestBody @Valid MemberSignUpRequest request) {
        memberService.signUp(request);
        return ResponseEntity.ok(ApiResponse.success(null, "회원가입이 완료되었습니다."));
    }

    // 회원 정보 조회
    @GetMapping("/userdetail")
    public ResponseEntity<ApiResponse<MemberResponse>> getUserDetails(@RequestHeader("Authorization") String authorizationHeader) {
        String email = extractEmail(authorizationHeader);

        if (email == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("이메일을 추출할 수 없습니다.", HttpStatus.BAD_REQUEST));
        }

        MemberResponse userResponse = memberService.getUserDetails(email);
        return ResponseEntity.ok(ApiResponse.success(null, userResponse));
    }

    // 회원 정보 업데이트
    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<String>> updateUser(@Valid @RequestBody MemberUpdateRequest request,
                                                          @RequestHeader("Authorization") String authorizationHeader) {
        String email = extractEmail(authorizationHeader);

        if (email == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("이메일을 추출할 수 없습니다.", HttpStatus.BAD_REQUEST));
        }

        memberService.updateUser(email, request);
        return ResponseEntity.ok(ApiResponse.success(null, "회원정보가 수정되었습니다."));
    }

    // 회원 탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteUser(@RequestHeader("Authorization") String authorizationHeader) {
        String email = extractEmail(authorizationHeader);

        if (email == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("이메일을 추출할 수 없습니다.", HttpStatus.BAD_REQUEST));
        }

        memberService.deleteUser(email);
        return ResponseEntity.ok(ApiResponse.success(null, "회원탈퇴가 완료되었습니다."));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        memberService.logout(token);
        return ResponseEntity.ok(ApiResponse.success(null, "로그아웃 완료되었습니다."));
    }

    // 이메일 추출 메서드
    private String extractEmail(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        return jwtTokenUtil.extractUsername(token);
    }
}