package com.BANnerIt.server.global.auth;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Component
@RequiredArgsConstructor
public class CustomGoogleIdTokenVerifier {

    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    public GoogleIdToken verify(String idTokenString) throws GeneralSecurityException, IOException {
        return googleIdTokenVerifier.verify(idTokenString);
    }
}