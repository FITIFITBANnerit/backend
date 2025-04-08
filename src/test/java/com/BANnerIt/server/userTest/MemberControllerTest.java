package com.BANnerIt.server.userTest;

import com.BANnerIt.server.api.user.controller.MemberController;
import com.BANnerIt.server.api.user.dto.MemberResponse;
import com.BANnerIt.server.api.user.dto.MemberUpdateRequest;
import com.BANnerIt.server.api.user.service.MemberService;
import com.BANnerIt.server.global.auth.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private MemberService memberService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new MemberController(memberService, jwtTokenUtil)).build();
    }

    @Test
    void 유저_정보를_수정한다() throws Exception {
        // given
        final String token = "validToken";
        final String authHeader = "Bearer " + token;

        final MemberUpdateRequest request = new MemberUpdateRequest(
                "test@example.com", "Updated User", "Updated User", "New Profile", true
        );

        given(memberService.extractUserId(authHeader)).willReturn(1L);
        given(memberService.updateUser(eq(1L), any(MemberUpdateRequest.class))).willReturn(true);

        // when & then
        mockMvc.perform(patch("/users/update")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_data").value("회원정보가 수정되었습니다."));
    }

    @Test
    void 유저를_탈퇴한다() throws Exception {
        // given
        final String token = "validToken";
        given(memberService.deleteMember(token)).willReturn(true);

        // when & then
        mockMvc.perform(delete("/users/delete")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value(nullValue()))
                .andExpect(jsonPath("$.user_data").value("회원탈퇴가 완료되었습니다."))
                .andExpect(jsonPath("$.error").value(nullValue()));
    }

    @Test
    void logout_로그아웃을_한다() throws Exception {
        // given
        final String token = "validToken";
        given(memberService.logout(token)).willReturn(true);

        // when & then
        mockMvc.perform(post("/users/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value(nullValue()))
                .andExpect(jsonPath("$.user_data").value("로그아웃 완료되었습니다."))
                .andExpect(jsonPath("$.error").value(nullValue()));
    }

    @Test
    void getUserDetails_성공적으로_조회한다() throws Exception {
        // given
        final String token = "validToken";
        final String authHeader = "Bearer " + token;
        final Long userId = 1L;

        final MemberResponse response = new MemberResponse(
                1L, "test@example.com", "테스트 유저", "USER", "프로필 이미지"
        );

        given(memberService.extractUserId(authHeader)).willReturn(userId);
        given(memberService.getUserDetails(userId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/users/userdetail")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_data.email").value("test@example.com"))
                .andExpect(jsonPath("$.user_data.name").value("테스트 유저"))
                .andExpect(jsonPath("$.user_data.userProfileUrl").value("프로필 이미지"))
                .andExpect(jsonPath("$.error").value(nullValue()));
    }
}