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
        Long userId = jwtTokenUtil.extractUserId(token);

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Report report = reportRepository.findById(request.report_id())
                .orElseThrow(()->new RuntimeException("Report not found"));

        report.setReviewedBy(member);

        List<Banner> banners = report.getBanners();

        for(BannerInfoDto bannerInfo : request.banner_info()){
            Banner banner = banners.stream()
                    .filter(b -> b.getBannerId().equals(bannerInfo.banner_id())) // bannerId가 일치하는 현수막 찾기
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Banner not found"));

            banner.setStatus(bannerInfo.status());
            bannerRepository.save(banner);
        }
        report.setStatus(ReportStatus.ADMIN_CONFIRMED);
        report.setUpdatedAt(ZonedDateTime.now());

        reportRepository.save(report);
    }


}
