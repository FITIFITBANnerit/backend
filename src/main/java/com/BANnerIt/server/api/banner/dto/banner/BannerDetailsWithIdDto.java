package com.BANnerIt.server.api.banner.dto.banner;

import com.BANnerIt.server.api.banner.domain.BannerStatus;

public record BannerDetailsWithIdDto(Long bannerId,
                                     BannerStatus status,
                                     String category,
                                     String companyName,
                                     String phoneNumber) {
}
