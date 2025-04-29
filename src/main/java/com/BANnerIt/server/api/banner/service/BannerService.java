package com.BANnerIt.server.api.banner.service;

import com.BANnerIt.server.api.banner.domain.Banner;
import com.BANnerIt.server.api.banner.domain.Report;
import com.BANnerIt.server.api.banner.domain.ReportStatus;
import com.BANnerIt.server.api.banner.dto.banner.BannerDetailsDto;
import com.BANnerIt.server.api.banner.dto.banner.BannerInfoDto;
import com.BANnerIt.server.api.banner.dto.banner.SaveBannerRequest;
import com.BANnerIt.server.api.banner.dto.banner.UpdateBannerRequest;
import com.BANnerIt.server.api.banner.repository.BannerRepository;
import com.BANnerIt.server.api.banner.repository.ReportRepository;
import com.BANnerIt.server.api.user.domain.Member;
import com.BANnerIt.server.api.user.repository.MemberRepository;
import com.BANnerIt.server.global.auth.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BannerService {
    private final BannerRepository bannerRepository;
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenUtil jwtTokenUtil;

    //프론트에 알림가게 하는거 x
    /*현수막 라벨링 정보 저장*/
    @Async
    @Transactional
    public void saveBanner(SaveBannerRequest request) {
        log.info("saveBanner 시작 - report_id: {}, banner_list 크기: {}",
                request.report_id(),
                request.banner_list() != null ? request.banner_list().size() : "null");

        Report report = reportRepository.findById(request.report_id())
                .orElseThrow(() -> {
                    log.warn("해당 report_id로 Report를 찾을 수 없음: {}", request.report_id());
                    return new RuntimeException("Report not found");
                });

        assert request.banner_list() != null;
        for (BannerDetailsDto bannerDetails : request.banner_list()) {
            try {
                log.debug("Banner 저장 시도: {}", bannerDetails);

                Banner banner = Banner.builder()
                        .report(report)
                        .status(bannerDetails.status())
                        .category(bannerDetails.category())
                        .companyName(bannerDetails.company_name())
                        .phoneNumber(bannerDetails.phone_number())
                        .build();

                bannerRepository.save(banner);
                log.debug("Banner 저장 성공: {}", banner);

            } catch (Exception e) {
                log.error("Banner 저장 실패 - 입력값: {}, 오류: {}", bannerDetails, e.getMessage(), e);
                throw e;
            }
        }

        log.info("saveBanner 완료 - report_id: {}", request.report_id());
        report.setStatus(ReportStatus.COMPLETED);
        reportRepository.save(report);
    }

    /*현수막 정보 수정*/
    @Transactional
    public void updateBanner(String token, UpdateBannerRequest request){
        log.info("[updateBanner] 호출됨 - token: {}, request: {}", token, request);

        Long userId = jwtTokenUtil.extractUserId(token);
        log.info("[updateBanner] 추출된 userId: {}", userId);

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("[updateBanner] userId: {} 에 해당하는 사용자 없음", userId);
                    return new RuntimeException("User not found");
                });
        log.info("[updateBanner] 사용자 조회 완료 - member: {}", member);

        Report report = reportRepository.findById(request.report_id())
                .orElseThrow(() -> {
                    log.error("[updateBanner] reportId: {} 에 해당하는 보고서 없음", request.report_id());
                    return new RuntimeException("Report not found");
                });
        log.info("[updateBanner] 보고서 조회 완료 - reportId: {}", request.report_id());

        report.setReviewedBy(member);

        List<Banner> banners = report.getBanners();
        log.info("[updateBanner] 해당 보고서의 현수막 수: {}", banners.size());

        for(BannerInfoDto bannerInfo : request.banner_info()){
            log.info("[updateBanner] 처리 중인 bannerInfo: {}", bannerInfo);

            Banner banner = banners.stream()
                    .filter(b -> b.getBannerId().equals(bannerInfo.banner_id()))
                    .findFirst()
                    .orElseThrow(() -> {
                        log.error("[updateBanner] bannerId: {} 에 해당하는 현수막 없음", bannerInfo.banner_id());
                        return new RuntimeException("Banner not found");
                    });

            log.info("[updateBanner] 현수막 조회 완료 - bannerId: {}, 이전 상태: {}", banner.getBannerId(), banner.getStatus());
            banner.setStatus(bannerInfo.status());
            bannerRepository.save(banner);
            log.info("[updateBanner] 현수막 상태 업데이트 완료 - bannerId: {}, 새로운 상태: {}", banner.getBannerId(), banner.getStatus());
        }

        report.setStatus(ReportStatus.ADMIN_CONFIRMED);
        report.setUpdatedAt(ZonedDateTime.now());
        reportRepository.save(report);
        log.info("[updateBanner] 보고서 상태 및 시간 업데이트 완료 - reportId: {}, status: {}", report.getReportId(), report.getStatus());
    }


}
