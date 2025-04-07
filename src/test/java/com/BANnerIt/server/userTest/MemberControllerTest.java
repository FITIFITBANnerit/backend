package com.BANnerIt.server.userTest;

import com.BANnerIt.server.api.user.controller.MemberController;
import com.BANnerIt.server.api.user.dto.MemberResponse;
import com.BANnerIt.server.api.user.dto.MemberSignUpRequest;
import com.BANnerIt.server.api.user.dto.MemberUpdateRequest;
import com.BANnerIt.server.api.user.service.MemberService;
import com.BANnerIt.server.global.auth.JwtTokenUtil;
import com.BANnerIt.server.global.exception.CustomException;
import com.BANnerIt.server.global.exception.ErrorCode;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        mockMvc = MockMvcBuilders.standaloneSetup(new MemberController(memberService, jwtTokenUtil))
                .defaultRequest(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
                .build();
    }

    @Test
    void 유저_정보를_수정한다() throws Exception {
        // given
        String token = "validToken";

        MemberUpdateRequest request = new MemberUpdateRequest(
                "test@example.com",
                "Updated User",
                "Updated User",
                "New Profile",
                true
        );


        when(memberService.extractUserId("Bearer " + token)).thenReturn(1L);

        // when & then
        mockMvc.perform(patch("/users/update")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_data").value("회원정보가 수정되었습니다."));

        verify(memberService).updateUser(eq(1L), any(MemberUpdateRequest.class));
    }


    @Test
    void 유저를_탈퇴한다() throws Exception {
        // given
        String token = "validToken";

        when(jwtTokenUtil.extractUserId(token)).thenReturn(1L);
        doNothing().when(memberService).deleteMember(token);

        // when & then
        mockMvc.perform(delete("/users/delete")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value((Object) null))
                .andExpect(jsonPath("$.user_data").value("회원탈퇴가 완료되었습니다."))
                .andExpect(jsonPath("$.error").value((Object) null));

        verify(memberService, times(1)).deleteMember(eq(token));
    }

    @Test
    void logout_로그아웃을_한다() throws Exception {
        String token = "validToken";

        mockMvc.perform(post("/users/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value(nullValue()))
                .andExpect(jsonPath("$.user_data").value("로그아웃 완료되었습니다."))
                .andExpect(jsonPath("$.error").value(nullValue()));

        verify(memberService, times(1)).logout(eq(token));
    }

    @Test
    void getUserDetails_성공적으로_조회한다() throws Exception {
        // given
        String token = "validToken";
        String authorizationHeader = "Bearer " + token;

        Long extractedUserId = 1L;
        MemberResponse response = new MemberResponse(
                1L,
                "test@example.com",
                "테스트 유저",
                "USER",
                "프로필 이미지"
        );
        given(memberService.extractUserId(authorizationHeader)).willReturn(extractedUserId);
        given(memberService.getUserDetails(extractedUserId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/users/userdetail")
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_data.email").value("test@example.com"))
                .andExpect(jsonPath("$.user_data.name").value("테스트 유저"))
                .andExpect(jsonPath("$.user_data.userProfileUrl").value("프로필 이미지"))
                .andExpect(jsonPath("$.user_data.isDeleted").value(true))
                .andExpect(jsonPath("$.error").value(nullValue()));

        verify(memberService, times(1)).extractUserId(authorizationHeader);
        verify(memberService, times(1)).getUserDetails(extractedUserId);
    }

}