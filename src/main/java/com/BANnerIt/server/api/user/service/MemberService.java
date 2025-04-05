package com.BANnerIt.server.api.user.service;

import com.BANnerIt.server.api.user.domain.Member;
import com.BANnerIt.server.api.user.dto.MemberResponse;
import com.BANnerIt.server.api.user.dto.MemberSignUpRequest;
import com.BANnerIt.server.api.user.dto.MemberUpdateRequest;
import com.BANnerIt.server.api.user.repository.MemberRepository;
import com.BANnerIt.server.global.auth.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public MemberResponse getUserDetails(Long userId) {
        Member member = memberRepository.findById(userId).orElse(null);

        if (member == null) {
            return null;
        }

        return member.toMemberResponse();
    }

    public boolean updateUser(Long userId, MemberUpdateRequest request) {
        Member member = memberRepository.findById(userId).orElse(null);

        if (member == null) {
            return false;
        }

        member.updateUser(request);
        memberRepository.saveAndFlush(member);
        return true;
    }

    public boolean deleteMember(String token) {
        final Long userId = jwtTokenUtil.extractUserId(token);

        Member member = memberRepository.findById(userId).orElse(null);

        if (member == null) {
            return false;
        }

        memberRepository.delete(member);
        jwtTokenUtil.addToBlacklist(token);
        return true;
    }

    public Long extractUserId(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authorizationHeader.replace("Bearer ", "").trim();

        return jwtTokenUtil.extractUserId(token);
    }

    public boolean logout(String token) {
        final Long userId = jwtTokenUtil.extractUserId(token);

        boolean exists = memberRepository.existsById(userId);

        if (!exists) {
            return false;
        }

        jwtTokenUtil.addToBlacklist(token);
        return true;
    }

    public void signUp(MemberSignUpRequest request) {
        Member newMember = Member.builder()
                .email(request.email())
                .password(request.password())
                .name(request.name())
                .build();

        memberRepository.save(newMember);
    }

    public Member findMemberById(Long userId) {
        return memberRepository.findById(userId).orElse(null);
    }
}