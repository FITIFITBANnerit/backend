package com.BANnerIt.server.api.user;

import com.BANnerIt.server.api.user.dto.MemberResponse;
import com.BANnerIt.server.api.user.dto.MemberUpdateRequest;
import com.BANnerIt.server.api.user.dto.UserData;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public UserData toUserData() {
        return new UserData(this.name, this.email, this.userProfile);
    }

    // Member 클래스에 MemberResponse 변환 메서드를 추가
    public MemberResponse toMemberResponse() {
        return new MemberResponse(
                this.userId,
                this.email,
                this.name,
                this.role,
                this.userProfile
        );
    }

    // Getter methods are not necessary since you don't want to use them
    public Long getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }
}