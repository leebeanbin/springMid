package com.sparta.springmid.product.service;

import com.sparta.springmid.global.config.MailManager;
import com.sparta.springmid.global.jwt.TokenProvider;
import com.sparta.springmid.global.util.SHA256Util;
import com.sparta.springmid.product.dto.TokenDto;
import com.sparta.springmid.product.model.User;
import com.sparta.springmid.product.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 로그인 인증 관련 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService{
    /**
     * 관련 클래스 호출
     */
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final MailManager mailManager;
    private static String magickey="";


    /**
     * 토큰 재발급 메서드
     * @param refreshToken
     * @return
     */
    @Transactional
    public TokenDto reissue(String refreshToken) {
        Optional<User> user = userRepository.findByRefreshToken(refreshToken);
        if(user!=null && !user.get().getRefreshToken().equals(refreshToken)){
            throw new RuntimeException("잘못된 토큰입니다.");
        }else if(user.get().isExpired()){
            throw new RuntimeException("폐지된 토큰입니다.");
        }
        Authentication authentication = tokenProvider.getAuthentication(refreshToken.substring(7));
        TokenDto tokenDto = tokenProvider.generateToken(authentication);
        user.get().updateToken(tokenDto.getRefreshToken());
        return tokenDto;
    }

    /**
     * 메일 전송 메서드
     * @param email
     * @return
     */
    public ResponseEntity<String> sendMail(String email){
        UUID uuid = UUID.randomUUID();
        String key = uuid.toString().substring(0,7);
        String sub = "인증번호 메일 전송";
        String content = "인증번호 : " + key;
        mailManager.send(email, sub, content);
        magickey = SHA256Util.getEncrypt(key, email);
        log.info(magickey);
        return ResponseEntity.ok(key);
    }

    /**
     * 메일 인증 코드 검증 메서드
     * @param key
     * @param email
     * @return
     */
    @Transactional
    public ResponseEntity<String> checkMail(String key, String email){
        String insertKey = SHA256Util.getEncrypt(key, email);
        if (!magickey.equals(insertKey)){
            return ResponseEntity.status(403).body("잘못된 키 입력입니다");
        }
        return ResponseEntity.status(202).body("인증 완료");
    }

}


