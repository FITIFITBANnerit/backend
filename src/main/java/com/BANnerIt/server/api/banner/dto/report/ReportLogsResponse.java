package com.BANnerIt.server.api.banner.dto.report;

import com.BANnerIt.server.api.banner.dto.ErrorDetails;

import java.util.List;

public record ReportLogsResponse(ErrorDetails error, List<ReportLogDto> reportLogs) {
}
