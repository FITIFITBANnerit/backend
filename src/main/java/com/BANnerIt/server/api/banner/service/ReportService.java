package com.BANnerIt.server.api.banner.service;

import com.BANnerIt.server.api.banner.domain.Banner;
import com.BANnerIt.server.api.banner.domain.Report;
import com.BANnerIt.server.api.banner.domain.ReportStatus;
import com.BANnerIt.server.api.banner.dto.banner.BannerDetailsWithIdDto;
import com.BANnerIt.server.api.banner.dto.report.*;
import com.BANnerIt.server.api.banner.repository.ReportRepository;
import com.BANnerIt.server.api.s3.domain.Image;
import com.BANnerIt.server.api.s3.service.S3Service;
import com.BANnerIt.server.api.user.domain.Member;
import com.BANnerIt.server.api.user.repository.MemberRepository;
import com.BANnerIt.server.global.auth.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;
    private final JwtTokenUtil jwtTokenUtil;

    /*현수막 신고 저장*/
    @Transactional
    public Long saveReport(String token, SaveReportRequest request) {
        log.info("saveReport() called with token: {}", token);

        try {
            ReportDto reportLog = request.report_log();
            log.debug("Extracted report_log: {}", reportLog);

            LocationDto location = reportLog.location();
            AddressDto address = reportLog.address();

            log.debug("Location: latitude={}, longitude={}", location.latitude(), location.longitude());
            log.debug("Address: {}, {}, {}", address.address1(), address.address2(), address.address3());

            Long userId = jwtTokenUtil.extractUserId(token);
            log.info("Extracted userId from token: {}", userId);

            Member member = memberRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("User not found for userId: {}", userId);
                        return new RuntimeException("User not found");
                    });

            Report report = Report.builder()
                    .content(reportLog.content())
                    .latitude(location.latitude())
                    .longitude(location.longitude())
                    .address1(address.address1())
                    .address2(address.address2())
                    .address3(address.address3())
                    .createdAt(ZonedDateTime.now())
                    .updatedAt(ZonedDateTime.now())
                    .status(ReportStatus.RECEIVED)
                    .createdBy(member)
                    .build();

            reportRepository.save(report);
            log.info("Report saved. ID: {}", report.getReportId());

            for (String key : reportLog.image_keys()) {
                Image image = Image.builder()
                        .imageKey(key)
                        .report(report)
                        .build();

                report.getImages().add(image);
                log.debug("Image added to report: {}", key);
            }

            return report.getReportId();

        } catch (Exception e) {
            log.error("Error in saveReport: {}", e.getMessage(), e);
            throw e;
        }
    }

    /*사용자의 현수막 신고기록 조회*/
    @Transactional
    public List<ReportLogDto> getUserReportLogs(String token){
        Long userId = jwtTokenUtil.extractUserId(token);
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return member.getCreatedReports().stream()
                .map(this::getReportLogDto)
                .toList();
    }

    /*전체 현수막 신고기록 조회*/
    @Transactional
    public List<ReportLogDto> getAllReportLogs() {
        List<Report> reports = reportRepository.findAll();

        return reports.stream()
                .map(this::getReportLogDto)
                .collect(Collectors.toList());
    }

    // 신고 기록을 ReportLogDto로 변환
    private ReportLogDto getReportLogDto(Report r) {
        LocationDto location = new LocationDto(r.getLatitude(), r.getLongitude());
        List<BannerDetailsWithIdDto> banners = convertBannersToDto(r.getBanners());
        List<String> urls = r.getImages().stream()
                .map(Image::getImageKey)
                .collect(Collectors.toList());
        List<String> images = s3Service.generateGetPresignedUrls(urls);

        Long createdById = Optional.ofNullable(r.getCreatedBy())
                .map(Member::getUserId)
                .orElse(null);

        return new ReportLogDto(r.getReportId(), r.getCreatedAt(),
                r.getStatus(), createdById, images, location, r.getContent(), banners);
    }

    // 배너 리스트를 DTO로 변환
    private static List<BannerDetailsWithIdDto> convertBannersToDto(List<Banner> banners) {
        return banners.stream()
                .map(b -> new BannerDetailsWithIdDto(b.getBannerId(),
                        b.getStatus(), b.getCategory(), b.getCompanyName(), b.getPhoneNumber()))
                .collect(Collectors.toList());
    }
}