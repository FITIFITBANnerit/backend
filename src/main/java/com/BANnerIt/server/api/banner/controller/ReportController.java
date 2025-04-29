package com.BANnerIt.server.api.banner.controller;

import com.BANnerIt.server.api.banner.dto.report.ReportLogsResponse;
import com.BANnerIt.server.api.banner.dto.report.SaveReportRequest;
import com.BANnerIt.server.api.banner.service.AiClinetService;
import com.BANnerIt.server.api.banner.service.BannerService;
import com.BANnerIt.server.api.banner.service.ReportService;
import com.BANnerIt.server.global.exception.ApiResponseUtil;
import com.BANnerIt.server.global.exception.CustomException;
import com.BANnerIt.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {
    private final ReportService reportService;
    private final AiClinetService aiClinetService;
    private final BannerService bannerService;

    /*현수막 신고 저장*/
    @PostMapping("/save")
    public ResponseEntity<?> createReport(@RequestHeader("Authorization") String authorizationHeader,
                                                            @RequestBody SaveReportRequest request){
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            Long reportId = reportService.saveReport(token, request);

            List<String> imageKeys = request.report_log().image_keys();

            aiClinetService.sendImageUrlsToAiServer(reportId, imageKeys)
                    .thenAcceptAsync(saveBannerRequest -> {
                        if (saveBannerRequest != null &&
                                saveBannerRequest.banner_list() != null &&
                                !saveBannerRequest.banner_list().isEmpty()) {

                            bannerService.saveBanner(saveBannerRequest);
                        }
                    })
                    .exceptionally(e -> {
                        log.error("AI 서버 예외 발생: {}", e.getMessage(), e);
                        return null;
                    });

            return ApiResponseUtil.ok("report_id", reportId);
        } catch (IllegalArgumentException e) {
            // 잘못된 Authorization 헤더 또는 토큰 오류 처리
            return ApiResponseUtil.fail("올바르지 않은 JWT입니다", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            // 그 외 예외 처리
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");
        }

    }

    /*사용자의 현수막 신고기록 조회*/
    @GetMapping("/logs/me")
    public ResponseEntity<?> findReportLogsByUser(@RequestHeader("Authorization") String authorizationHeader){
        try {
            String token = authorizationHeader.replace("Bearer ", "");

            ReportLogsResponse response = new ReportLogsResponse(reportService.getUserReportLogs(token));

            return ApiResponseUtil.ok("report_logs", response.report_logs());
        } catch (IllegalArgumentException e) {
            // 잘못된 Authorization 헤더 또는 토큰 오류 처리
            return ApiResponseUtil.fail("올바르지 않은 JWT입니다", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            // 그 외 예외 처리
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");
        }
    }

    /*전체 현수막 신고기록 조회*/
    @GetMapping("/logs")
    public ResponseEntity<?> findAllReportLogs(){
        try {
            ReportLogsResponse response = new ReportLogsResponse(reportService.getAllReportLogs());

            return ApiResponseUtil.ok("report_logs", response.report_logs());
        }  catch (Exception e) {
            // 그 외 예외 처리
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");
        }
    }
}
