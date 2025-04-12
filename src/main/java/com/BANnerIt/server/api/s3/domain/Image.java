package com.BANnerIt.server.api.s3.domain;

import com.BANnerIt.server.api.banner.domain.Report;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    private String imageKey;

    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;
}
