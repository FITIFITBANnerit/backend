package com.BANnerIt.server.api.banner.repository;

import com.BANnerIt.server.api.banner.domain.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long> {
}
