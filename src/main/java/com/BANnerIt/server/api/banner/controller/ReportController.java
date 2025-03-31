package com.BANnerIt.server.api.banner.controller;

<<<<<<< HEAD
=======
import com.BANnerIt.server.api.banner.dto.ErrorResponse;
>>>>>>> 03b4283acb1dfbb9a775d40c332eb97e163801a9
import com.BANnerIt.server.api.banner.dto.report.ReportLogsResponse;
import com.BANnerIt.server.api.banner.dto.report.SaveReportRequest;
import com.BANnerIt.server.api.banner.service.ReportService;
import com.BANnerIt.server.global.exception.ApiResponse;
import com.BANnerIt.server.global.exception.CustomException;
import com.BANnerIt.server.global.exception.ErrorCode;
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
    public ResponseEntity<ApiResponse<Long>> createReport(@RequestHeader("Authorization") String authorizationHeader,
                                                    @RequestBody SaveReportRequest request){
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            Long userId = reportService.saveReport(token, request);

            return ResponseEntity.ok(ApiResponse.ok(userId));
        } catch (IllegalArgumentException e) {
            // 잘못된 Authorization 헤더 또는 토큰 오류 처리
<<<<<<< HEAD
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("올바르지 않은 JWT입니다", HttpStatus.UNAUTHORIZED));
        } catch (Exception e) {
            // 그 외 예외 처리
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");
=======
            SaveReportResponse errorResponse = new SaveReportResponse(new ErrorResponse(400, "Bad Request: Invalid or missing token"), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            // 그 외 예외 처리
            SaveReportResponse errorResponse = new SaveReportResponse(new ErrorResponse(500, "Internal Server Error"), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
>>>>>>> 03b4283acb1dfbb9a775d40c332eb97e163801a9
        }
    }

    /*사용자의 현수막 신고기록 조회*/
    @GetMapping("/logs/me")
<<<<<<< HEAD
    public ResponseEntity<ApiResponse<ReportLogsResponse>> findReportLogsByUser(@RequestHeader("Authorization") String authorizationHeader){
=======
    public ResponseEntity<ReportLogsResponse> findReportLogsByUser(@RequestHeader("Authorization") String authorizationHeader){
>>>>>>> 03b4283acb1dfbb9a775d40c332eb97e163801a9
        try {
            String token = authorizationHeader.replace("Bearer ", "");

            ReportLogsResponse response = new ReportLogsResponse(reportService.getUserReportLogs(token));

            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (IllegalArgumentException e) {
            // 잘못된 Authorization 헤더 또는 토큰 오류 처리
<<<<<<< HEAD
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("올바르지 않은 JWT입니다", HttpStatus.UNAUTHORIZED));
        } catch (Exception e) {
            // 그 외 예외 처리
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");
=======
            ReportLogsResponse errorResponse = new ReportLogsResponse(new ErrorResponse(400, "Bad Request: Invalid or missing token"), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            // 그 외 예외 처리
            ReportLogsResponse errorResponse = new ReportLogsResponse(new ErrorResponse(500, "Internal Server Error"), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
>>>>>>> 03b4283acb1dfbb9a775d40c332eb97e163801a9
        }
    }

    /*전체 현수막 신고기록 조회*/
    @GetMapping("/logs")
<<<<<<< HEAD
    public ResponseEntity<ApiResponse<ReportLogsResponse>> findAllReportLogs(){
=======
    public ResponseEntity<ReportLogsResponse> findAllReportLogs(){
>>>>>>> 03b4283acb1dfbb9a775d40c332eb97e163801a9
        try {
            ReportLogsResponse response = new ReportLogsResponse(reportService.getAllReportLogs());

            return ResponseEntity.ok(ApiResponse.ok(response));
        }  catch (Exception e) {
            // 그 외 예외 처리
<<<<<<< HEAD
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");
=======
            ReportLogsResponse errorResponse = new ReportLogsResponse(new ErrorResponse(500, "Internal Server Error"), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
>>>>>>> 03b4283acb1dfbb9a775d40c332eb97e163801a9
        }
    }
}
