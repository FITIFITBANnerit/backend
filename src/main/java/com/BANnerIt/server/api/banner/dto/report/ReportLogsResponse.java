package com.BANnerIt.server.api.banner.dto.report;

import com.BANnerIt.server.global.exception.ApiResponse;

import java.util.List;

public record ReportLogsResponse(List<ReportLogDto> reportLogs) {
}
