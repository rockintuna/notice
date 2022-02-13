package me.rockintuna.notice.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import me.rockintuna.notice.domain.User;

import java.security.Key;
import java.util.Date;

public class JwtTokenProvider {

    private final Key secretKey;

    public JwtTokenProvider(String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public static final long ACCESS_TOKEN_USETIME = 2 * 60 * 60 * 1000L;

    public String responseAccessToken(User user) {
        return createToken(user, ACCESS_TOKEN_USETIME);
    }

    private String createToken(User user, Long expirationDuration) {
        Date now = new Date();

        return Jwts.builder()
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("username", user.getUsername())
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationDuration))
                .compact();
    }

    public Claims getClaims(String token) {

        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}