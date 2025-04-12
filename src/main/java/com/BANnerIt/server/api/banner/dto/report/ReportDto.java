package com.BANnerIt.server.api.banner.dto.report;

import java.util.List;

public record ReportDto(LocationDto location,
                        AddressDto address,
                        List<String> image_keys,
                        String content) {
}
