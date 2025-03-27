package com.BANnerIt.server.api.banner.repository;

import com.BANnerIt.server.api.banner.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
