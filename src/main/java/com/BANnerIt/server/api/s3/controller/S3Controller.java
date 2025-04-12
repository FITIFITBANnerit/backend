package com.BANnerIt.server.api.s3.controller;

import com.BANnerIt.server.api.s3.dto.PresignedUrlRequest;
import com.BANnerIt.server.api.s3.dto.PresignedUrlResponse;
import com.BANnerIt.server.api.s3.service.S3Service;
import com.BANnerIt.server.global.exception.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
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
        return ApiResponseUtil.ok("key_urls", s3Service.generatePresignedUrls(request, folder));
    }
}
