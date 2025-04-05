package com.BANnerIt.server.api.banner.service;

import com.BANnerIt.server.api.banner.domain.Banner;
import com.BANnerIt.server.api.banner.domain.Report;
import com.BANnerIt.server.api.banner.dto.banner.BannerDetailsDto;
import com.BANnerIt.server.api.banner.dto.banner.BannerInfoDto;
import com.BANnerIt.server.api.banner.dto.banner.SaveBannerRequest;
import com.BANnerIt.server.api.banner.dto.banner.UpdateBannerRequest;
import com.BANnerIt.server.api.banner.repository.BannerRepository;
import com.BANnerIt.server.api.banner.repository.ReportRepository;
import com.BANnerIt.server.api.user.Member;
import com.BANnerIt.server.api.user.repository.MemberRepository;
import com.BANnerIt.server.global.auth.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class BannerService {
    private final BannerRepository bannerRepository;
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenUtil jwtTokenUtil;

    //reportstatus 바꾸는거 아직 추가 x, 프론트에 알림가게 하는거 x
    //ai에서 분석한 배너가 어떤 reportId인지 확인할 방법 아직 x..(사진id로 확인?,,아마도)
    /*현수막 라벨링 정보 저장*/
    @Transactional
    public void saveBanner(SaveBannerRequest request){
        Report report = reportRepository.findById(request.reportId())
                .orElseThrow(()->new RuntimeException("Report not found"));

        for(BannerDetailsDto bannerDetails : request.bannerList()){
            Banner banner = Banner.builder()
                    .report(report)
                    .status(bannerDetails.status())
                    .category(bannerDetails.category())
                    .companyName(bannerDetails.companyName())
                    .phoneNumber(bannerDetails.phoneNumber())
                    .build();

            bannerRepository.save(banner);
        }
    }

    /*현수막 정보 수정*/
    public void updateBanner(String token, UpdateBannerRequest request){
        Long userId = jwtTokenUtil.extractUserId(token);

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Report report = reportRepository.findById(request.reportId())
                .orElseThrow(()->new RuntimeException("Report not found"));

        report.setReviewedBy(member);

        List<Banner> banners = report.getBanners();

        for(BannerInfoDto bannerInfo : request.bannerInfoList()){
            Banner banner = banners.stream()
                    .filter(b -> b.getBannerId().equals(bannerInfo.bannerId())) // bannerId가 일치하는 현수막 찾기
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Banner not found"));

            banner.setStatus(bannerInfo.status());
            bannerRepository.save(banner);
        }

        reportRepository.save(report);
    }
}
