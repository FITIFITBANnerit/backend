package com.BANnerIt.server.api.banner.dto.banner;

import com.BANnerIt.server.api.banner.domain.BannerStatus;

public record BannerInfoDto(Long banner_id, BannerStatus status) {
}
