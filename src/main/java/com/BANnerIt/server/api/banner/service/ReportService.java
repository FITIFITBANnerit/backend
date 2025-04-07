package com.BANnerIt.server.api.banner.service;

import com.BANnerIt.server.api.banner.domain.Banner;
import com.BANnerIt.server.api.banner.domain.Report;
import com.BANnerIt.server.api.banner.domain.ReportStatus;
import com.BANnerIt.server.api.banner.dto.banner.BannerDetailsWithIdDto;
import com.BANnerIt.server.api.banner.dto.report.*;
import com.BANnerIt.server.api.banner.repository.ReportRepository;
import com.BANnerIt.server.api.user.domain.Member;
import com.BANnerIt.server.api.user.repository.MemberRepository;
import com.BANnerIt.server.global.auth.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenUtil jwtTokenUtil;

    //사진 저장 아직 안함
    /*현수막 신고 저장*/
    @Transactional
    public Long saveReport(String token, SaveReportRequest request){
        ReportDto reportLog = request.report();
        LocationDto location = reportLog.location();
        AddressDto address = reportLog.address();

        Long userId = jwtTokenUtil.extractUserId(token);
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Report report = Report.builder()
                .content(reportLog.content())
                .latitude(location.latitude())
                .longitude(location.longitude())
                .address1(address.address1())
                .address2(address.address2())
                .address3(address.address3())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(ReportStatus.RECEIVED)
                .createdBy(member)
                .build();

        reportRepository.save(report);
        return report.getReportId();
    }

    /*사용자의 현수막 신고기록 조회*/
    public List<ReportLogDto> getUserReportLogs(String token){
        Long userId = jwtTokenUtil.extractUserId(token);
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return member.getCreatedReports().stream()
                .map(ReportService::getReportLogDto)
                .toList();
    }

    /*전체 현수막 신고기록 조회*/
    public List<ReportLogDto> getAllReportLogs() {
        List<Report> reports = reportRepository.findAll();

        return reports.stream()
                .map(ReportService::getReportLogDto)
                .collect(Collectors.toList());
    }

    // 신고 기록을 ReportLogDto로 변환
    private static ReportLogDto getReportLogDto(Report r) {
        LocationDto location = new LocationDto(r.getLatitude(), r.getLongitude());
        List<BannerDetailsWithIdDto> banners = convertBannersToDto(r.getBanners());

        return new ReportLogDto(r.getReportId(), r.getCreatedAt(),
                r.getStatus(), location, r.getContent(), banners);
    }

    // 배너 리스트를 DTO로 변환
    private static List<BannerDetailsWithIdDto> convertBannersToDto(List<Banner> banners) {
        return banners.stream()
                .map(b -> new BannerDetailsWithIdDto(b.getBannerId(),
                        b.getStatus(), b.getCategory(), b.getCompanyName(), b.getPhoneNumber()))
                .collect(Collectors.toList());
    }
}