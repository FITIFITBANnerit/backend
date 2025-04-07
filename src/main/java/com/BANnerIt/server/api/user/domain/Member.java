package com.BANnerIt.server.api.user.domain;

import com.BANnerIt.server.api.banner.domain.Report;
import com.BANnerIt.server.api.user.dto.MemberResponse;
import com.BANnerIt.server.api.user.dto.MemberUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column
    private String password;

    @Column
    private String userProfile;

    @Column(nullable = false)
    private boolean serviceAccept = true;

    @Column(nullable = false)
    private LocalDate userRegisteredAt;

    @Column
    private LocalDate userModifiedAt;

    @Column(nullable = false, length = 50)
    private String platformType;

    @Column(nullable = false, length = 50)
    private String role;

    @OneToMany(mappedBy = "createdBy")
    private List<Report> createdReports;

    @OneToMany(mappedBy = "reviewedBy")
    private List<Report> reviewedReports;

    @Builder
    public Member(String email, String name, String password, String userProfile, boolean serviceAccept, String platformType, String role, LocalDate userRegisteredAt, LocalDate userModifiedAt) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.userProfile = userProfile;
        this.serviceAccept = serviceAccept;
        this.userRegisteredAt = userRegisteredAt != null ? userRegisteredAt : LocalDate.now();
        this.platformType = platformType != null ? platformType : "google";
        this.role = role != null ? role : "USER";
        this.userModifiedAt = userModifiedAt;
    }

    public void updateUser(MemberUpdateRequest request) {
        if (request.name() != null) {
            this.name = request.name();
        }
        if (request.userProfile() != null) {
            this.userProfile = request.userProfile();
        }
        this.userModifiedAt = LocalDate.now();
    }

    public MemberResponse toMemberResponse() {
        return new MemberResponse(
                this.userId,
                this.email,
                this.name,
                this.role,
                this.userProfile
        );
    }
}