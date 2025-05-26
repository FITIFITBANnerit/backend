package com.BANnerIt.server.api.banner.dto.banner;

import com.BANnerIt.server.api.banner.domain.BannerStatus;

import java.util.List;

public record BannerDetailsWithIdDto(Long banner_id,
                                     BannerStatus status,
                                     String category,
                                     String company_name,
                                     String phone_number,
                                     List<Float> center,
                                     float width,
                                     float height) {
}
