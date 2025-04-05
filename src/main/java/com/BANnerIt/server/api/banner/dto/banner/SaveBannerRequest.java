package com.BANnerIt.server.api.banner.dto.banner;

import java.util.List;
public record SaveBannerRequest(Long reportId,
                                List<BannerDetailsDto> bannerList) {
}
