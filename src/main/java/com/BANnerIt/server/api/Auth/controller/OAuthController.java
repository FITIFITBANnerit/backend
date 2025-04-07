package com.BANnerIt.server.api.Auth.controller;

import com.BANnerIt.server.api.Auth.dto.AutoLoginResponse;
import com.BANnerIt.server.api.Auth.service.OAuthService;
import com.BANnerIt.server.api.user.dto.UserData;
import com.BANnerIt.server.global.exception.ApiResponse;
import com.BANnerIt.server.global.exception.CustomException;
import com.BANnerIt.server.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/oauth")
public class OAuthController {
    private final OAuthService oAuthService;

    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<?>> validateIdToken(@RequestBody Map<String, String> request) {
        String idToken = request.get("id_token");
        try {
            Map<String, Object> userDetails = oAuthService.authenticateUser(idToken);

            String jwtToken = (String) userDetails.get("accessToken");
            UserData userData = (UserData) userDetails.get("userData");

            if (jwtToken == null || userData == null) {
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "OAuth 인증 후 필요한 정보를 가져오지 못했습니다.");
            }

            return ResponseEntity.ok(ApiResponse.success(jwtToken, userData));
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

        return ResponseEntity.ok(ApiResponse.success(result.jwt(), result.userData()));
    }


}