package com.BANnerIt.server.global.auth;

import com.BANnerIt.server.api.user.Member;
import com.BANnerIt.server.api.user.dto.MemberResponse;
import com.BANnerIt.server.api.user.repository.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public UserDetails loadUserById(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
        MemberResponse memberResponse = member.toMemberResponse();

        return new org.springframework.security.core.userdetails.User(
                memberResponse.getEmail(),
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + member.getRole()))
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}