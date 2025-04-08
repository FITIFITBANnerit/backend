package com.BANnerIt.server.api.s3.controller;

import com.BANnerIt.server.api.s3.dto.PresignedUrlRequest;
import com.BANnerIt.server.api.s3.dto.PresignedUrlResponse;
import com.BANnerIt.server.api.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/presigned-urls")
public class S3Controller {
    private final S3Service s3Service;

    @PostMapping
    public PresignedUrlResponse createPresignedUrls(
            @RequestBody PresignedUrlRequest request,
            @RequestParam(defaultValue = "report") String folder
    ) {
        return s3Service.generatePresignedUrls(request, folder);
    }
}
