package com.BANnerIt.server.api.user.controller;

import com.BANnerIt.server.api.user.dto.UserData;
import com.BANnerIt.server.api.user.service.OAuthService;
import com.BANnerIt.server.global.exception.ApiResponse;
import com.BANnerIt.server.global.exception.CustomException;
import com.BANnerIt.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {

    private final OAuthService oAuthService;

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<?>> validateIdToken(@RequestBody Map<String, String> request) {
        String idToken = request.get("id_token");
        try {
            Map<String, Object> userDetails = oAuthService.authenticateUser(idToken);
            String jwtToken = (String) userDetails.get("jwt");
            UserData userData = (UserData) userDetails.get("user_data");

            return ResponseEntity.ok(ApiResponse.success(jwtToken, userData));
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "승인되지 않은 접근입니다.");
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");
        }
    }

}