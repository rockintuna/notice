package me.rockintuna.notice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.rockintuna.notice.configuration.JwtTokenProvider;
import me.rockintuna.notice.dto.RegisterUserRequestDto;
import me.rockintuna.notice.dto.UserLoginRequestDto;
import me.rockintuna.notice.dto.UserLoginResponseDto;
import me.rockintuna.notice.dto.UserResponseDto;
import me.rockintuna.notice.exception.LoginFailureException;
import me.rockintuna.notice.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("회원 가입 성공")
    void register() throws Exception {
        //given
        RegisterUserRequestDto registerUserRequestDto =
                new RegisterUserRequestDto("tester", "tester@notice.test", "password");
        String body = objectMapper.writeValueAsString(registerUserRequestDto);
        UserResponseDto responseDto = UserResponseDto.builder()
                .username(registerUserRequestDto.getUsername())
                .email(registerUserRequestDto.getEmail())
                .id(1L).build();
        given(userService.register(any(RegisterUserRequestDto.class))).willReturn(responseDto);

        //when
        mvc.perform(post("/api/user")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.username").value(registerUserRequestDto.getUsername()))
                .andExpect(jsonPath("$.email").value(registerUserRequestDto.getEmail()));
    }

    @Test
    @DisplayName("회원 가입 실패 - 이메일 없음")
    void registerWithoutEmail() throws Exception {
        //given
        RegisterUserRequestDto registerUserRequestDto =
                new RegisterUserRequestDto("tester", null, "password");
        String body = objectMapper.writeValueAsString(registerUserRequestDto);

        //when
        mvc.perform(post("/api/user")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())

                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 가입 실패 - 사용자 이름 없음")
    void registerWithoutName() throws Exception {
        //given
        RegisterUserRequestDto registerUserRequestDto =
                new RegisterUserRequestDto(null, "tester@notice.test", "password");
        String body = objectMapper.writeValueAsString(registerUserRequestDto);

        //when
        mvc.perform(post("/api/user")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())

                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 가입 실패 - 패스워드 없음")
    void registerWithoutPassword() throws Exception {
        //given
        RegisterUserRequestDto registerUserRequestDto =
                new RegisterUserRequestDto("tester", "tester@notice.test", null);
        String body = objectMapper.writeValueAsString(registerUserRequestDto);

        //when
        mvc.perform(post("/api/user")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())

                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 성공")
    void login() throws Exception {
        //given
        UserLoginRequestDto userLoginRequestDto =
                new UserLoginRequestDto("tester@notice.test", "password");
        String body = objectMapper.writeValueAsString(userLoginRequestDto);
        String accessToken = "12345abcde";
        UserLoginResponseDto responseDto = new UserLoginResponseDto(accessToken);
        given(userService.login(any(UserLoginRequestDto.class))).willReturn(responseDto);

        //when
        mvc.perform(post("/api/login")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(responseDto.getAccessToken()));
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 계정")
    void loginEmailNotFound() throws Exception {
        //given
        UserLoginRequestDto userLoginRequestDto =
                new UserLoginRequestDto("tester@notice.test", "password");
        String body = objectMapper.writeValueAsString(userLoginRequestDto);

        given(userService.login(any(UserLoginRequestDto.class))).
                willThrow(new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        //when
        mvc.perform(post("/api/login")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())

                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 실패 - 패스워드 불일치")
    void loginPasswordFailure() throws Exception {
        //given
        UserLoginRequestDto userLoginRequestDto =
                new UserLoginRequestDto("tester@notice.test", "password");
        String body = objectMapper.writeValueAsString(userLoginRequestDto);

        given(userService.login(any(UserLoginRequestDto.class))).
                willThrow(new LoginFailureException("비밀번호가 일치하지 않습니다."));

        //when
        mvc.perform(post("/api/login")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())

                //then
                .andExpect(status().isBadRequest());
    }
}