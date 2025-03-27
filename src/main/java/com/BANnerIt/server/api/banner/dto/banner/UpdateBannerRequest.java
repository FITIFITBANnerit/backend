package com.BANnerIt.server.api.banner.dto.banner;

import java.util.List;
public record UpdateBannerRequest(Long reportId,
                                  List<BannerInfoDto> bannerInfoList) {
}
