package com.BANnerIt.server.api.banner.dto.banner;

import com.BANnerIt.server.api.banner.domain.BannerStatus;

public record BannerDetailsWithIdDto(Long banner_id,
                                     BannerStatus status,
                                     String category,
                                     String company_name,
                                     String phone_number) {
}
