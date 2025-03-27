package com.BANnerIt.server.api.banner.dto.report;

import com.BANnerIt.server.api.banner.dto.ErrorResponse;

import java.util.List;

public record ReportLogsResponse(ErrorResponse error, List<ReportLogDto> reportLogs) {
}
