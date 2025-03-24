package com.BANnerIt.server.api.user.service;

import com.BANnerIt.server.api.user.Member;
import com.BANnerIt.server.api.user.dto.UserData;
import com.BANnerIt.server.api.user.repository.MemberRepository;
import com.BANnerIt.server.api.user.service.IdTokenVerify;
import com.BANnerIt.server.global.auth.JwtTokenUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService {

    private final JwtTokenUtil jwtTokenUtil;
    private final MemberRepository memberRepository;
    private final IdTokenVerify idTokenVerify;

    public Map<String, Object> authenticateUser(String idToken) throws GeneralSecurityException, IOException {
        GoogleIdToken.Payload payload = idTokenVerify.verifyIdToken(idToken);

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");

        log.info("Google OAuth: email = {}", email);

        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> registerNewUser(email, name, pictureUrl));

        String jwtToken = jwtTokenUtil.generateToken(email);
        UserData userData = new UserData(name, email, pictureUrl);

        Map<String, Object> response = new HashMap<>();
        response.put("jwt", jwtToken);
        response.put("user_data", userData);

        return response;
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
        return memberRepository.save(newUser);
    }
}