package com.BANnerIt.server.api.banner.dto.report;

import com.BANnerIt.server.api.banner.dto.ErrorResponse;

public record SaveReportResponse(ErrorResponse error, Long reportId) {
}
