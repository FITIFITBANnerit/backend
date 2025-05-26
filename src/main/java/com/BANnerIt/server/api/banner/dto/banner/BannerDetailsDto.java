package com.BANnerIt.server.api.banner.dto.banner;

import com.BANnerIt.server.api.banner.domain.BannerStatus;
import java.util.List;

public record BannerDetailsDto(BannerStatus status,
                               String category,
                               String company_name,
                               String phone_number,
                               List<Float> center,
                               float width,
                               float height) {
}
