package com.BANnerIt.server.api.banner.service;

import com.BANnerIt.server.api.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiClinetService {
    private final RestTemplate restTemplate;
    private final S3Service s3Service;

    private String aiServerUrl = "http://www.bannerit-ai.p-e.kr/";

    @Async
    public void sendImageUrlsToAiServer(Long reportId, List<String> imageKeys) {
        log.info("📤 sendImageUrlsToAiServer() called with reportId: {}, imageKeys: {}", reportId, imageKeys);

        String sendUrl = aiServerUrl + "analyze";
        try {
            log.debug("🧾 Generating presigned URLs from S3 keys...");
            List<String> imageUrls = s3Service.generateGetPresignedUrls(imageKeys);
            log.debug("✅ Generated presigned URLs: {}", imageUrls);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("report_id", reportId);
            requestBody.put("image_urls", imageUrls);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("🚀 Sending POST to AI server at: {}",sendUrl);
            log.debug("📦 Request Body: {}", requestBody);
            log.debug("🧾 Headers: {}", headers);

            ResponseEntity<String> response = restTemplate.postForEntity(sendUrl, entity, String.class);

            log.info("✅ AI 서버 응답 성공: status={}, body={}", response.getStatusCode(), response.getBody());

        } catch (Exception e) {
            log.error("❌ AI 서버 전송 실패!");
            log.error("🛑 실패 원인: {}", e.getMessage(), e);
        }
    }
}
