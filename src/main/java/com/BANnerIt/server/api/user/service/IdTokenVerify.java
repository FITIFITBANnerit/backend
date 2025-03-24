package com.BANnerIt.server.api.user.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdTokenVerify {

    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    public GoogleIdToken.Payload verifyIdToken(String idToken) throws GeneralSecurityException, IOException {
        GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);

        if (googleIdToken == null) {
            throw new IllegalArgumentException("유효하지 않은 ID token 입니다.");
        }

        return googleIdToken.getPayload();
    }
}