package com.sparta.areadevelopment.jwt;

import com.sparta.areadevelopment.dto.TokenDto;
import com.sparta.areadevelopment.entity.CustomUserDetails;
import com.sparta.areadevelopment.enums.AuthEnum;
import com.sparta.areadevelopment.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenProvider {

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 14;// 2주
    String token = AuthEnum.GRANT_TYPE.getValue();
    private final Key key;
    private CustomUserDetailsService detailsService;

    public TokenProvider(@Value("${JWT_SECRET_KEY}") String secretKey,
            CustomUserDetailsService detailsService) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.detailsService = detailsService;
    }

    /**
     * 유저 정보를 통해 토큰 생성
     */
    public TokenDto generateToken(Authentication authentication) {
        log.info("generateToken start");

        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME); // 30분
        Date refreshTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPIRE_TIME); // 14일

        String accessToken = token + Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", "USER")
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .setIssuedAt(new Date(now))
                .compact();

        log.info(accessToken);

        String refreshToken = token + Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .setIssuedAt(new Date(now))
                .setSubject(authentication.getName())
                .compact();

        log.info("accessToken: {}", accessToken);
        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();


    }

    /**
     * 토큰에서 유저 정보 추출
     */
    public Authentication getAuthentication(String token) {
        String username = parseClaims(token).getSubject();
        CustomUserDetails userDetails = detailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "",
                userDetails.getAuthorities());
    }


    /**
     * 토큰 정보 검증
     */
    public boolean validateToken(String token) {
        log.info("validateToken start");
        log.info("token: {}", token);
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            // refresh token 활용해서 재발급
            log.info("Expired JWT Token", e);
            throw e;
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String getUsername(String refreshToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();
        return claims.getSubject();
    }
}
