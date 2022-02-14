package me.rockintuna.notice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@RequiredArgsConstructor
public class UserLoginRequestDto {
    @NotEmpty
    private final String email;
    @NotEmpty
    private final String password;
}
