package me.rockintuna.notice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {
    private final Long id;
    private final String email;
    private final String username;
}

