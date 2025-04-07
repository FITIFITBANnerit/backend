package com.BANnerIt.server.api.banner.dto.banner;

import java.util.List;
public record SaveBannerRequest(Long report_id,
                                List<BannerDetailsDto> banner_list) {
}
