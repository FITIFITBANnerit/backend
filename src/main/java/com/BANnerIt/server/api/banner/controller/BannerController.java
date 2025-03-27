package com.BANnerIt.server.api.banner.controller;

import com.BANnerIt.server.api.banner.dto.ErrorDetails;
import com.BANnerIt.server.api.banner.dto.banner.SaveBannerRequest;
import com.BANnerIt.server.api.banner.dto.banner.UpdateBannerRequest;
import com.BANnerIt.server.api.banner.service.BannerService;
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
    public ResponseEntity<ErrorDetails> createBanners(@RequestHeader("Authorization") String authorizationHeader,
                                                      SaveBannerRequest request) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");

            bannerService.saveBanner(request);

            return ResponseEntity.ok(null);
        } catch (IllegalArgumentException e) {
            // 잘못된 Authorization 헤더 또는 토큰 오류 처리
            ErrorDetails errorResponse = new ErrorDetails(400, "Bad Request: Invalid or missing token");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }catch (Exception e) {
            // 그 외 예외 처리
            ErrorDetails errorResponse = new ErrorDetails(500, "Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /*현수막 정보 수정*/
    @PutMapping("/update")
    public ResponseEntity<ErrorDetails> updateBanners(@RequestHeader("Authorization") String authorizationHeader,
                                                      UpdateBannerRequest request) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");

            bannerService.updateBanner(request);

            return ResponseEntity.ok(null);
        } catch (IllegalArgumentException e) {
            // 잘못된 Authorization 헤더 또는 토큰 오류 처리
            ErrorDetails errorResponse = new ErrorDetails(400, "Bad Request: Invalid or missing token");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }catch (Exception e) {
            // 그 외 예외 처리
            ErrorDetails errorResponse = new ErrorDetails(500, "Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
