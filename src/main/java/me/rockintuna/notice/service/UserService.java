package me.rockintuna.notice.service;

import me.rockintuna.notice.dto.RegisterUserRequestDto;
import me.rockintuna.notice.dto.UserLoginRequestDto;
import me.rockintuna.notice.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import me.rockintuna.notice.configuration.JwtTokenProvider;
import me.rockintuna.notice.domain.User;
import me.rockintuna.notice.dto.UserLoginResponseDto;
import me.rockintuna.notice.exception.LoginFailureException;
import me.rockintuna.notice.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return UserDetailsImpl.from(user);
    }

    public UserLoginResponseDto login(UserLoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if(!passwordEncoder.matches(requestDto.getPassword(), user.getPassword()))
            throw new LoginFailureException("비밀번호가 일치하지 않습니다.");

        return UserLoginResponseDto.builder()
                .accessToken(jwtTokenProvider.responseAccessToken(user))
                .build();
    }

    public UserResponseDto register(RegisterUserRequestDto requestDto) {
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        User user = User.create(requestDto, encodedPassword);
        User createdUser = userRepository.save(user);
        return UserResponseDto.builder()
                .id(createdUser.getId())
                .email(createdUser.getEmail())
                .username(createdUser.getUsername())
                .build();
    }
}
