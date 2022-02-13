package me.rockintuna.notice.controller;

import me.rockintuna.notice.dto.RegisterUserRequestDto;
import me.rockintuna.notice.dto.UserLoginRequestDto;
import me.rockintuna.notice.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import me.rockintuna.notice.dto.UserLoginResponseDto;
import me.rockintuna.notice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/user")
    public ResponseEntity<UserResponseDto> login(@RequestBody RegisterUserRequestDto requestDto) {
        return ResponseEntity.ok(userService.register(requestDto));
    }

    @PostMapping("/api/login")
    public ResponseEntity<UserLoginResponseDto> login(@RequestBody UserLoginRequestDto requestDto) {
        return ResponseEntity.ok(userService.login(requestDto));
    }
}
