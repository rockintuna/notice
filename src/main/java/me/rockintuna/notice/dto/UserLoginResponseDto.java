package me.rockintuna.notice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserLoginResponseDto {
    private final String accessToken;
}
