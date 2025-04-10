package com.BANnerIt.server.api.Auth.service;

import com.BANnerIt.server.api.Auth.verifier.IdTokenVerify;
import com.BANnerIt.server.api.Auth.domain.RefreshToken;
import com.BANnerIt.server.api.Auth.repository.RefreshTokenRepository;
import com.BANnerIt.server.api.Auth.dto.AutoLoginResponse;
import com.BANnerIt.server.api.user.domain.Member;
import com.BANnerIt.server.api.user.dto.UserData;
import com.BANnerIt.server.api.user.repository.MemberRepository;
import com.BANnerIt.server.global.auth.JwtTokenUtil;
import com.BANnerIt.server.global.exception.CustomException;
import com.BANnerIt.server.global.exception.ErrorCode;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class OAuthService {

    private final JwtTokenUtil jwtTokenUtil;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final IdTokenVerify idTokenVerify;

    public OAuthService(JwtTokenUtil jwtTokenUtil, MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository, IdTokenVerify idTokenVerify) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.memberRepository = memberRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.idTokenVerify=idTokenVerify;
    }

    public AutoLoginResponse authenticateUser(String idToken) throws GeneralSecurityException, IOException {
        GoogleIdToken.Payload payload = idTokenVerify.verifyIdToken(idToken);
        if (payload == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "유효하지 않은 ID 토큰입니다.");
        }

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");

        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> registerNewUser(email, name, pictureUrl));

        String accessToken = jwtTokenUtil.generateAccessToken(member.getUserId());
        String refreshToken = jwtTokenUtil.generateRefreshToken(member.getUserId());

        refreshTokenRepository.save(new RefreshToken(member.getUserId(), refreshToken));

        UserData userData = new UserData(
                member.getUserId(),
                member.getRole(),
                member.getName(),
                member.getEmail(),
                member.getUserProfile()
        );

        return new AutoLoginResponse(accessToken, userData);
    }

    public AutoLoginResponse autoLogin(String accessToken) {
        try {
            if (jwtTokenUtil.validateToken(accessToken)) {
                return getUserDataFromToken(accessToken);
            }
        } catch (ExpiredJwtException e) {
        }

        Long userId = jwtTokenUtil.extractUserIdFromExpiredToken(accessToken);
        return refreshAccessToken(userId);
    }


    public String extractAccessTokenFromHeader(HttpServletRequest request) {
        return jwtTokenUtil.resolveToken(request);
    }


    public AutoLoginResponse refreshAccessToken(Long userId) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REFRESH_TOKEN, "RefreshToken을 찾을 수 없습니다."));

        if (!jwtTokenUtil.validateToken(refreshToken.getToken())) {
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN, "RefreshToken이 만료되었습니다.");
        }

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER, "사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtTokenUtil.generateAccessToken(userId);

        UserData userData = new UserData(
                member.getUserId(),
                member.getRole(),
                member.getName(),
                member.getEmail(),
                member.getUserProfile()
        );

        return new AutoLoginResponse(newAccessToken, userData);
    }


    private AutoLoginResponse getUserDataFromToken(String token) {
        Long userId = jwtTokenUtil.extractUserId(token);
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER, "사용자를 찾을 수 없습니다."));

        UserData userData = new UserData(
                member.getUserId(),
                member.getRole(),
                member.getName(),
                member.getEmail(),
                member.getUserProfile()
        );

        return new AutoLoginResponse(token, userData);
    }

    private Member registerNewUser(String email, String name, String pictureUrl) {
        Member newUser = Member.builder()
                .email(email)
                .name(name)
                .password(null)
                .userProfile(pictureUrl)
                .serviceAccept(true)
                .platformType("google")
                .role("USER")
                .build();

        Member savedUser = memberRepository.save(newUser);
        String refreshToken = jwtTokenUtil.generateRefreshToken(savedUser.getUserId());
        refreshTokenRepository.save(new RefreshToken(savedUser.getUserId(), refreshToken));

        return savedUser;
    }
}