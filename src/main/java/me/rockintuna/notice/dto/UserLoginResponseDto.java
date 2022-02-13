package me.rockintuna.notice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLoginResponseDto {
    private final String accessToken;
}
