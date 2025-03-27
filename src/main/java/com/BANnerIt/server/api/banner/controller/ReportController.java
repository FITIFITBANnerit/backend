package com.BANnerIt.server.api.banner.controller;

import com.BANnerIt.server.api.banner.dto.ErrorResponse;
import com.BANnerIt.server.api.banner.dto.report.ReportLogsResponse;
import com.BANnerIt.server.api.banner.dto.report.SaveReportRequest;
import com.BANnerIt.server.api.banner.dto.report.SaveReportResponse;
import com.BANnerIt.server.api.banner.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    /*현수막 신고 저장*/
    @PostMapping("/save")
    public ResponseEntity<SaveReportResponse> createReport(@RequestHeader("Authorization") String authorizationHeader,
                                                           @RequestBody SaveReportRequest request){
        try {
            String token = authorizationHeader.replace("Bearer ", "");

            SaveReportResponse response = new SaveReportResponse(null, reportService.saveReport(token, request));

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // 잘못된 Authorization 헤더 또는 토큰 오류 처리
            SaveReportResponse errorResponse = new SaveReportResponse(new ErrorResponse(400, "Bad Request: Invalid or missing token"), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            // 그 외 예외 처리
            SaveReportResponse errorResponse = new SaveReportResponse(new ErrorResponse(500, "Internal Server Error"), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /*사용자의 현수막 신고기록 조회*/
    @GetMapping("/logs/me")
    public ResponseEntity<ReportLogsResponse> findReportLogsByUser(@RequestHeader("Authorization") String authorizationHeader){
        try {
            String token = authorizationHeader.replace("Bearer ", "");

            ReportLogsResponse response = new ReportLogsResponse(null, reportService.getUserReportLogs(token));

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // 잘못된 Authorization 헤더 또는 토큰 오류 처리
            ReportLogsResponse errorResponse = new ReportLogsResponse(new ErrorResponse(400, "Bad Request: Invalid or missing token"), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            // 그 외 예외 처리
            ReportLogsResponse errorResponse = new ReportLogsResponse(new ErrorResponse(500, "Internal Server Error"), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /*전체 현수막 신고기록 조회*/
    @GetMapping("/logs")
    public ResponseEntity<ReportLogsResponse> findAllReportLogs(){
        try {
            ReportLogsResponse response = new ReportLogsResponse(null, reportService.getAllReportLogs());

            return ResponseEntity.ok(response);
        }  catch (Exception e) {
            // 그 외 예외 처리
            ReportLogsResponse errorResponse = new ReportLogsResponse(new ErrorResponse(500, "Internal Server Error"), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
