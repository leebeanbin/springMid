package com.sparta.areadevelopment.controller;

import com.sparta.areadevelopment.dto.KeyDto;
import com.sparta.areadevelopment.dto.TokenDto;
import com.sparta.areadevelopment.dto.UserLoginDto;
import com.sparta.areadevelopment.entity.CustomUserDetails;
import com.sparta.areadevelopment.enums.AuthEnum;
import com.sparta.areadevelopment.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 인증관련 Controller
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * 로그인 서비스 호출
     */
    private final AuthService authService;

    /**
     * 로그인 URL
     *
     * @param userLoginRequestDto
     * @param response            토큰 헤더 설정
     * @return 200 ok
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDto userLoginRequestDto,
            HttpServletResponse response) {
        String username = userLoginRequestDto.getUsername();
        String password = userLoginRequestDto.getPassword();
        TokenDto token = authService.login(username, password);
        response.setHeader(AuthEnum.ACCESS_TOKEN.getValue(), token.getAccessToken());
        response.setHeader(AuthEnum.REFRESH_TOKEN.getValue(), token.getRefreshToken());
        return ResponseEntity.ok("로그인 완료!");
    }

    /**
     * 리프레시 토큰을 통한 엑세스 리프레쉬 토큰 재발급 controller
     *
     * @param request  헤더의 토큰
     * @param response 새로운 토큰 세팅
     * @return 200 ok
     */
    @PostMapping("/reissue")
    public ResponseEntity<String> reissue(HttpServletRequest request,
            HttpServletResponse response) {
        String refreshToken = request.getHeader(AuthEnum.REFRESH_TOKEN.getValue());
        TokenDto token = authService.reissue(refreshToken);
        response.setHeader(AuthEnum.ACCESS_TOKEN.getValue(), token.getAccessToken());
        response.setHeader(AuthEnum.REFRESH_TOKEN.getValue(), token.getRefreshToken());
        return ResponseEntity.ok("재발급완료");
    }

    /**
     * 로그아웃 controller 시큐리티 config의 매서드 오출
     *
     * @param request
     * @param response
     * @param authentication
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        authService.logout(request, response, authentication);
        return ResponseEntity.ok("로그아웃완료");
    }

    /**
     * 메일 전송 controller
     *
     * @param customUserDetails 이메일 정보를 UserDetails로 받음
     * @return 200ok
     */
    @PostMapping("/send-mail")
    public ResponseEntity<String> sendMail(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return authService.sendMail(customUserDetails.getUser().getEmail());
    }

    /**
     * 이메일 인증 확인 controller
     *
     * @param key               인증 키
     * @param customUserDetails 유저 이메일
     * @return 200ok
     */
    @PutMapping("/check-mail")
    public ResponseEntity<String> checkMail(@RequestBody KeyDto key,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return authService.checkMail(key.getKey(), customUserDetails.getUser().getEmail());
    }
}
