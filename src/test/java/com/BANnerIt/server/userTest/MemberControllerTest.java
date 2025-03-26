package com.BANnerIt.server.userTest;

import com.BANnerIt.server.api.user.controller.MemberController;
import com.BANnerIt.server.api.user.dto.MemberSignUpRequest;
import com.BANnerIt.server.api.user.dto.MemberUpdateRequest;
import com.BANnerIt.server.api.user.service.MemberService;
import com.BANnerIt.server.global.auth.JwtTokenUtil;
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

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
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
    void signUpTest() throws Exception {
        MemberSignUpRequest request = new MemberSignUpRequest("test@example.com", "password", "Test User");

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"email\":\"test@example.com\", \"password\":\"password\", \"name\":\"Test User\"}"))
                .andExpect(jsonPath("$.user_data").value("회원가입이 완료되었습니다."))
                .andExpect(jsonPath("$.error").value(nullValue()));

        verify(memberService, times(1)).signUp(any(MemberSignUpRequest.class));
    }

    @Test
    void updateUserTest() throws Exception {
        String token = "validToken";
        String email = "test@example.com";

        MemberUpdateRequest request = new MemberUpdateRequest(
                email,
                "Updated User",
                "Updated User",
                "New Profile",
                true
        );

        when(jwtTokenUtil.extractUsername(token)).thenReturn(email);

        mockMvc.perform(patch("/users/update")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"mail\":\"" + email + "\", \"updatedUser\":\"Updated User\", \"name\":\"Updated User\", \"userProfile\":\"New Profile\", \"b\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_data").value("회원정보가 수정되었습니다."))
                .andExpect(jsonPath("$.error").value(nullValue()));

        verify(memberService, times(1)).updateUser(eq(email), any(MemberUpdateRequest.class));
    }

    @Test
    void deleteUserTest() throws Exception {
        String token = "validToken";
        String email = "test@example.com";

        when(jwtTokenUtil.extractUsername(token)).thenReturn(email);

        mockMvc.perform(delete("/users/delete", 1L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value(nullValue()))
                .andExpect(jsonPath("$.user_data").value("회원탈퇴가 완료되었습니다."))
                .andExpect(jsonPath("$.error").value(nullValue()));

        verify(memberService, times(1)).deleteUser(eq(email));
    }

    @Test
    void logoutTest() throws Exception {
        String token = "validToken";

        mockMvc.perform(post("/users/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value(nullValue()))
                .andExpect(jsonPath("$.user_data").value("로그아웃 완료되었습니다."))
                .andExpect(jsonPath("$.error").value(nullValue()));

        verify(memberService, times(1)).logout(eq(token));
    }
}