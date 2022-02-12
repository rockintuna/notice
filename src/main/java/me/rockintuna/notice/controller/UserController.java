package me.rockintuna.notice.controller;

import me.rockintuna.notice.dto.JwtResponseDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @PostMapping("/api/login")
    public JwtResponseDto login() {
        return null;
    }
}
