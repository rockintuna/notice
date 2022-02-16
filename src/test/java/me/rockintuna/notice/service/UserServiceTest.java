package me.rockintuna.notice.service;

import me.rockintuna.notice.configuration.JwtTokenProvider;
import me.rockintuna.notice.domain.User;
import me.rockintuna.notice.dto.RegisterUserRequestDto;
import me.rockintuna.notice.dto.UserLoginRequestDto;
import me.rockintuna.notice.dto.UserLoginResponseDto;
import me.rockintuna.notice.dto.UserResponseDto;
import me.rockintuna.notice.exception.LoginFailureException;
import me.rockintuna.notice.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("회원가입 성공")
    void register() {
        //given
        String encodedPassword = "encodedPassword";
        RegisterUserRequestDto registerUserRequestDto =
                new RegisterUserRequestDto("tester", "tester@notice.test", "password");
        given(passwordEncoder.encode(registerUserRequestDto.getPassword())).willReturn(encodedPassword);
        User user = User.create(registerUserRequestDto, encodedPassword);
        given(userRepository.save(any(User.class))).willReturn(user);

        //when
        UserResponseDto responseDto = userService.register(registerUserRequestDto);

        //then
        assertThat(responseDto.getEmail()).isEqualTo(registerUserRequestDto.getEmail());
        assertThat(responseDto.getUsername()).isEqualTo(registerUserRequestDto.getUsername());
    }

    @Test
    @DisplayName("로그인 성공")
    void login() {
        //given
        String receivedToken = "token";
        UserLoginRequestDto userLoginRequestDto =
                new UserLoginRequestDto("tester@notice.test", "password");
        RegisterUserRequestDto registerUserRequestDto =
                new RegisterUserRequestDto("tester", "tester@notice.test", "password");
        User user = User.create(registerUserRequestDto, "encodedPassword");
        given(userRepository.findByEmail(userLoginRequestDto.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(userLoginRequestDto.getPassword(), user.getPassword()))
                .willReturn(true);
        given(jwtTokenProvider.responseAccessToken(user)).willReturn(receivedToken);

        //when
        UserLoginResponseDto responseDto = userService.login(userLoginRequestDto);

        //then
        assertThat(responseDto.getAccessToken()).isEqualTo(receivedToken);
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 계정")
    void loginNotFoundUser() {
        //given
        UserLoginRequestDto userLoginRequestDto =
                new UserLoginRequestDto("tester@notice.test", "password");

        given(userRepository.findByEmail(userLoginRequestDto.getEmail())).willReturn(Optional.empty());

        //when, then
        assertThrows(UsernameNotFoundException.class,
                () -> userService.login(userLoginRequestDto));
    }

    @Test
    @DisplayName("로그인 실패 - 패스워드 불일치")
    void loginPasswordFailure() {
        //given
        UserLoginRequestDto userLoginRequestDto =
                new UserLoginRequestDto("tester@notice.test", "password");
        RegisterUserRequestDto registerUserRequestDto =
                new RegisterUserRequestDto("tester", "tester@notice.test", "password");
        User user = User.create(registerUserRequestDto, "encodedPassword");
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(userLoginRequestDto.getPassword(), user.getPassword()))
                .willReturn(false);

        //when, then
        assertThrows(LoginFailureException.class,
                () -> userService.login(userLoginRequestDto));
    }
}