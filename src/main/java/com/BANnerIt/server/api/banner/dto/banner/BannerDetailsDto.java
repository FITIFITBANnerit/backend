package com.BANnerIt.server.api.banner.dto.banner;

import com.BANnerIt.server.api.banner.domain.BannerStatus;

public record BannerDetailsDto(BannerStatus status,
                               String category,
                               String company_name,
                               String phone_number) {
}
