package com.BANnerIt.server.api.s3.controller;

import com.BANnerIt.server.api.s3.dto.PresignedUrlRequest;
import com.BANnerIt.server.api.s3.dto.PresignedUrlResponse;
import com.BANnerIt.server.api.s3.service.S3Service;
import com.BANnerIt.server.global.exception.ApiResponseUtil;
import com.BANnerIt.server.global.exception.CustomException;
import com.BANnerIt.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/presigned-urls")
public class S3Controller {
    private final S3Service s3Service;

    @PostMapping
    public ResponseEntity<?> createPresignedUrls(
            @RequestBody PresignedUrlRequest request,
            @RequestParam(defaultValue = "report") String folder
    ) {
        try{
            return ApiResponseUtil.ok("key_urls", s3Service.generatePutPresignedUrls(request, folder));
        }catch (IllegalArgumentException e) {
            // 잘못된 Authorization 헤더 또는 토큰 오류 처리
            return ApiResponseUtil.fail("올바르지 않은 JWT입니다", HttpStatus.UNAUTHORIZED);
        }catch (Exception e) {
            // 그 외 예외 처리
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");
        }
    }
}
