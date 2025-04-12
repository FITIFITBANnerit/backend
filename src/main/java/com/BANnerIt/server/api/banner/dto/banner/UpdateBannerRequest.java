package com.BANnerIt.server.api.banner.dto.banner;

import java.util.List;
public record UpdateBannerRequest(Long report_id,
                                  List<BannerInfoDto> banner_info) {
}
