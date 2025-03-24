package com.BANnerIt.server.api.user.service;

import com.BANnerIt.server.api.user.Member;
import com.BANnerIt.server.api.user.dto.MemberResponse;
import com.BANnerIt.server.api.user.dto.MemberSignUpRequest;
import com.BANnerIt.server.api.user.dto.MemberUpdateRequest;
import com.BANnerIt.server.api.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final IdTokenVerify idTokenVerify;

    // 로그인
    public Map<String, Object> login(String idToken) throws GeneralSecurityException, IOException {

        Map<String, Object> userDetails = idTokenVerify.verifyIdToken(idToken);

        String email = (String) userDetails.get("email");
        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    Member newUser = Member.builder()
                            .email(email)
                            .name((String) userDetails.get("name"))
                            .build();
                    memberRepository.save(newUser);
                    return newUser;
                });

        String accessToken = (String) userDetails.get("access_token");
        userDetails.put("access_token", accessToken);

        return userDetails;
    }

    // 회원가입
    public void signUp(MemberSignUpRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        Member newUser = Member.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .build();

        memberRepository.save(newUser);
    }

    //회원정보 조회
    public MemberResponse getUserDetails(String email) {
        Member member = findMemberByEmail(email);
        return member.toMemberResponse();
    }

    //email로 회원 정보 반환
    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    public void updateUser(String email, MemberUpdateRequest request) {
        Member user = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.updateUser(request);


        memberRepository.saveAndFlush(user);  // saveAndFlush()를 사용하여 즉시 반영
    }


    // 회원 탈퇴
    public void deleteUser(String email) {
        Member user = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        memberRepository.delete(user);
    }

    // 로그아웃
    public String logout(String token) {
        return "로그아웃 완료";
    }
}