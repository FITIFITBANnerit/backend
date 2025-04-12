package com.BANnerIt.server.api.banner.domain;

import com.BANnerIt.server.api.s3.domain.Image;
import com.BANnerIt.server.api.user.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
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

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private String content;

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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "report", cascade = CascadeType.ALL)
    private List<Banner> banners;

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();
}