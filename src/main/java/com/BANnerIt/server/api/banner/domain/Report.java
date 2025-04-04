package com.BANnerIt.server.api.banner.domain;

import com.BANnerIt.server.api.user.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Data
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String content;
    private String bannerUrl;

    private String address1;
    private String address2;
    private String address3;

    private Double latitude;
    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "reviewed_by")
    private Member reviewedBy;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Member createdBy;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "report")
    private List<Banner> banners;
}
