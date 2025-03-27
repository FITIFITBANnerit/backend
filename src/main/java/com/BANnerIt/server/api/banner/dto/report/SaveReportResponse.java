package com.BANnerIt.server.api.banner.dto.report;

import com.BANnerIt.server.api.banner.dto.ErrorDetails;

public record SaveReportResponse(ErrorDetails error, Long reportId) {
}
