package com.BANnerIt.server.api.Auth.controller;

import com.BANnerIt.server.api.Auth.dto.AutoLoginResponse;
import com.BANnerIt.server.api.Auth.service.OAuthService;
import com.BANnerIt.server.api.Auth.dto.UserData;
import com.BANnerIt.server.global.exception.ApiResponse;
import com.BANnerIt.server.global.exception.CustomException;
import com.BANnerIt.server.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/oauth")
public class OAuthController {

    private final OAuthService oAuthService;

    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<UserData>> validateIdToken(@RequestBody Map<String, String> request) {
        String idToken = request.get("id_token");

        try {
            AutoLoginResponse loginResponse = oAuthService.authenticateUser(idToken);

            return ResponseEntity
                    .ok()
                    .header("Authorization", "Bearer " + loginResponse.accessToken())
                    .body(ApiResponse.success(loginResponse.userData()));

        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "승인되지 않은 접근입니다.");
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<UserData>> refreshAccessToken(HttpServletRequest request) {
        final String accessToken = oAuthService.extractAccessTokenFromHeader(request);
        final AutoLoginResponse result = oAuthService.autoLogin(accessToken);

        return ResponseEntity
                .ok()
                .header("Authorization", "Bearer " + result.accessToken())
                .body(ApiResponse.success(result.userData()));
    }
}
