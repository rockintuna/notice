package me.rockintuna.notice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@RequiredArgsConstructor
public class RegisterUserRequestDto {
    @NotEmpty
    private final String username;
    @NotEmpty
    private final String email;
    @NotEmpty
    private final String password;
}
