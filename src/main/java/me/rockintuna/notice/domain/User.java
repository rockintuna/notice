package me.rockintuna.notice.domain;

import me.rockintuna.notice.dto.RegisterUserRequestDto;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User() {
    }

    public static User create(RegisterUserRequestDto requestDto, String encodedPassword) {
        return new User(
                requestDto.getUsername(),
                requestDto.getEmail(),
                encodedPassword);
    }
}
