package com.BANnerIt.server.api.s3.service;

import com.BANnerIt.server.api.s3.domain.Image;
import com.BANnerIt.server.api.s3.dto.PresignedUrlDto;
import com.BANnerIt.server.api.s3.dto.PresignedUrlRequest;
import com.BANnerIt.server.api.s3.dto.PresignedUrlResponse;
import com.BANnerIt.server.api.s3.repository.ImageRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;
    private final ImageRepository imageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public PresignedUrlResponse generatePresignedUrls(PresignedUrlRequest request, String folder) {
        List<PresignedUrlDto> urls = new ArrayList<>();

        for (String file : request.files()) {
            String uuid = UUID.randomUUID().toString();
            String key = folder + "/" + uuid + "." + file;

            Date expiration = Date.from(Instant.now().plusSeconds(60 * 5)); // 5분 유효

            GeneratePresignedUrlRequest s3UrlRequest = new GeneratePresignedUrlRequest(bucket, key)
                    .withMethod(com.amazonaws.HttpMethod.PUT)
                    .withExpiration(expiration);

            URL url = amazonS3.generatePresignedUrl(s3UrlRequest);

            urls.add(new PresignedUrlDto(key, url.toString()));
        }

        return new PresignedUrlResponse(urls);
    }
}
