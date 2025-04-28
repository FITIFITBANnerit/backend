package com.BANnerIt.server.api.banner.controller;

import com.BANnerIt.server.api.banner.dto.banner.SaveBannerRequest;
import com.BANnerIt.server.api.banner.dto.banner.UpdateBannerRequest;
import com.BANnerIt.server.api.banner.service.BannerService;
import com.BANnerIt.server.global.exception.ApiResponse;
import com.BANnerIt.server.global.exception.ApiResponseUtil;
import com.BANnerIt.server.global.exception.CustomException;
import com.BANnerIt.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/banners")
@RequiredArgsConstructor
public class BannerController {
    private final BannerService bannerService;

    /*현수막 정보 수정*/
    @PatchMapping("/update")
    public ResponseEntity<?> updateBanners(@RequestHeader("Authorization") String authorizationHeader,
                                                     @RequestBody UpdateBannerRequest request) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");

            bannerService.updateBanner(token, request);

            return ApiResponseUtil.ok("error", null);
        } catch (IllegalArgumentException e) {
            // 잘못된 Authorization 헤더 또는 토큰 오류 처리
            return ApiResponseUtil.fail("올바르지 않은 JWT입니다", HttpStatus.UNAUTHORIZED);
        }catch (Exception e) {
            // 그 외 예외 처리
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");
        }
    }
}
