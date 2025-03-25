package com.BANnerIt.server.api.user.service;

import com.BANnerIt.server.api.user.Member;
import com.BANnerIt.server.api.user.dto.MemberResponse;
import com.BANnerIt.server.api.user.dto.MemberUpdateRequest;
import com.BANnerIt.server.api.user.repository.MemberRepository;
import com.BANnerIt.server.api.user.service.IdTokenVerify;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final IdTokenVerify idTokenVerify;

    public MemberResponse getUserDetails(Long userId) {
        Member member = findMemberById(userId);
        return member.toMemberResponse();
    }

    public Member findMemberById(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    public void updateUser(Long userId, MemberUpdateRequest request) {
        Member user = findMemberById(userId);
        user.updateUser(request);
        memberRepository.saveAndFlush(user);
    }

    public void deleteUser(Long userId) {
        Member user = findMemberById(userId);
        memberRepository.delete(user);
    }

}