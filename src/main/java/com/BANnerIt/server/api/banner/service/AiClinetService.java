package com.BANnerIt.server.api.banner.service;

import com.BANnerIt.server.api.banner.domain.BannerStatus;
import com.BANnerIt.server.api.banner.dto.banner.BannerDetailsDto;
import com.BANnerIt.server.api.banner.dto.banner.SaveBannerRequest;
import com.BANnerIt.server.api.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiClinetService {
    private final RestTemplate restTemplate;
    private final S3Service s3Service;

    private final String aiServerUrl = "http://www.bannerit-ai.p-e.kr/";

    @Async
    public CompletableFuture<SaveBannerRequest> sendImageUrlsToAiServer(Long reportId, List<String> imageKeys) {
        log.info("📤 sendImageUrlsToAiServer() called with reportId: {}, imageKeys: {}", reportId, imageKeys);

        String sendUrl = aiServerUrl + "/analyze";
        //String sendUrl = "http://localhost:8080/mock-ai";

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

            log.info("🚀 Sending POST to AI server at: {}", sendUrl);
            log.debug("📦 Request Body: {}", requestBody);
            log.debug("🧾 Headers: {}", headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(sendUrl, entity, Map.class);

            log.info("✅ AI 서버 응답 성공: status={}, body={}", response.getStatusCode(), response.getBody());


            // AI 서버 응답에서 "banner_list" 추출
            Map<String, Object> responseBody = response.getBody();
            Object bannerListObj = responseBody.get("banner_list");

            List<BannerDetailsDto> bannerList = null;

            // AI 서버 응답에서 배너 리스트 추출
            if (bannerListObj != null) {
                bannerList = new ArrayList<>();
                List<Map<String, Object>> bannerListMap = (List<Map<String, Object>>) bannerListObj;
                for (Map<String, Object> bannerMap : bannerListMap) {
                    List<Double> center = (List<Double>) bannerMap.get("center");
                    BannerDetailsDto bannerDetails = new BannerDetailsDto(
                            BannerStatus.valueOf((String) bannerMap.get("status")),
                            (String) bannerMap.get("category"),
                            (String) bannerMap.get("company_name"),
                            (String) bannerMap.get("phone_number"),
                            List.of(center.get(0).floatValue(), center.get(1).floatValue()),
                            ((Number) bannerMap.get("width")).floatValue(),
                            ((Number) bannerMap.get("height")).floatValue()
                    );
                    bannerList.add(bannerDetails);
                }
            }

            // SaveBannerRequest 객체 생성
            SaveBannerRequest saveBannerRequest = new SaveBannerRequest(reportId, bannerList);

            return CompletableFuture.completedFuture(saveBannerRequest);

            } catch(Exception e){
                log.error("❌ AI 서버 전송 실패!");
                log.error("🛑 실패 원인: {}", e.getMessage(), e);
                throw new RuntimeException("AI 서버 통신 실패", e);
            }
    }
}
