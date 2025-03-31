package com.BANnerIt.server.api.banner.controller;

import com.BANnerIt.server.api.banner.dto.banner.SaveBannerRequest;
import com.BANnerIt.server.api.banner.dto.banner.UpdateBannerRequest;
import com.BANnerIt.server.api.banner.service.BannerService;
import com.BANnerIt.server.global.exception.ApiResponse;
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

    /*현수막 라벨링 정보 저장*/
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> createBanners(@RequestHeader("Authorization") String authorizationHeader,
                                                     SaveBannerRequest request) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");

            bannerService.saveBanner(request);

            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (IllegalArgumentException e) {
            // 잘못된 Authorization 헤더 또는 토큰 오류 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("올바르지 않은 JWT입니다", HttpStatus.UNAUTHORIZED));
        }catch (Exception e) {
            // 그 외 예외 처리
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");
        }
    }

    /*현수막 정보 수정*/
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateBanners(@RequestHeader("Authorization") String authorizationHeader,
                                                       UpdateBannerRequest request) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");

            bannerService.updateBanner(request);

            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (IllegalArgumentException e) {
            // 잘못된 Authorization 헤더 또는 토큰 오류 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("올바르지 않은 JWT입니다", HttpStatus.UNAUTHORIZED));
        }catch (Exception e) {
            // 그 외 예외 처리
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");
        }
    }
}
