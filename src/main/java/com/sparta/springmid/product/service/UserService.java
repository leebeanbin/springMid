package com.sparta.springmid.product.service;

import com.sparta.springmid.global.enums.AuthEnum;
import com.sparta.springmid.global.enums.StatusEnum;
import com.sparta.springmid.global.jwt.TokenProvider;
import com.sparta.springmid.product.dto.PasswordChangeRequestDto;
import com.sparta.springmid.product.dto.SignOutRequestDto;
import com.sparta.springmid.product.dto.SignupRequestDto;
import com.sparta.springmid.product.dto.TokenDto;
import com.sparta.springmid.product.dto.UpdateUserDto;
import com.sparta.springmid.product.dto.UserInfoDto;
import com.sparta.springmid.product.model.Board;
import com.sparta.springmid.product.model.User;
import com.sparta.springmid.product.model.Comment;
import com.sparta.springmid.product.repository.BoardRepository;
import com.sparta.springmid.product.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements LogoutHandler {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;

    /**
     * @Transactional 을 걸어 저장 실패의 경우는 롤백
     * @param requestDto
     * @return 등록 성공한 유저의 id 반환
     */
    @Transactional
    public Long signUp(SignupRequestDto requestDto) {
        User user = new User();

        user.serUserInfo(requestDto.getUsername(),
                requestDto.getNickname(),
                passwordEncoder.encode(requestDto.getPassword()),
                requestDto.getEmail(),
                requestDto.getInfo()
        );
        return userRepository.save(user).getId();
    }

    public UserInfoDto getUserProfile(Long userId, User user) {
        getUserDetails(userId, user);

        return new UserInfoDto(user.getUsername(), user.getNickname(),
                user.getInfo(), user.getEmail());
    }


    @Transactional
    public void updateProfile(Long userId, UpdateUserDto requestDto, User user) {
        // customUserDetails를 이용해서, 유저를 찾고 검증 로직을 안에다 넣자
        getUserDetails(userId, user);
        checkPassword(user.getPassword(), requestDto.getPassword());
        user.updateInfo(requestDto);
    }

    public void updatePassword(Long userId, PasswordChangeRequestDto requestDto,
            User user) {

        getUserDetails(userId, user);
        checkPassword(user.getPassword(), requestDto.getOldPassword()); // 저장되어 있는 비밀번호와 맞는지 검증
        user.updatePassword(passwordEncoder.encode(requestDto.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public TokenDto login(String username, String password) {
        Optional<User> user = userRepository.findUserByUsernameAndStatus(username, StatusEnum.ACTIVE);
        checkPassword(user.get().getPassword(), password);

        // 이 객체는 인증 프로세스의 초기 단계에서 사용자의 인증 정보를 담고 있으며,
        // Authentication 인터페이스를 구현하고 있습니다.
        // 이 토큰은 인증을 위해 필요한 주요 정보(주로 사용자 이름과 비밀번호)를 포함합니다.
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username,password);

        // 인증 된 정보를 기반으로 Manager에서 검증을 진행합니다.
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        log.debug("SecurityContext에 Authentication 저장.");
        // user 활성화 -> login
        user.get().setExpired(false);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        TokenDto tokenDto = tokenProvider.generateToken(authentication);

        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        user.get().updateToken(tokenDto.getRefreshToken());

        return new TokenDto(AuthEnum.GRANT_TYPE.getValue(), tokenDto.getAccessToken(),
                tokenDto.getRefreshToken(), false);
    }

    /**
     * 로그아웃 메서드
     * @param request
     * @param response
     * @param authentication
     */
    @Transactional
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response , Authentication authentication) {
        String authHeader = request.getHeader(AuthEnum.ACCESS_TOKEN.getValue());
        // 억세스 토큰 검증
        if (authHeader == null && !authHeader.startsWith(AuthEnum.GRANT_TYPE.getValue())) {
            throw new RuntimeException("알수 없는 access token.");
        }

        // 전반부 식별자 제거
        String accessToken = authHeader.substring(7);
        // 토큰에서 이름 가져온다.
        String username = tokenProvider.getUsername(accessToken);

        Optional<User> userStatus = userRepository.findUserByUsernameAndStatus(username, StatusEnum.ACTIVE);
        // user 로그아웃 -> user 비활성화
        userStatus.get().setExpired(true);
    }

    public void signOut(Long userId, SignOutRequestDto requestDto, User user) {
        getUserDetails(userId, user);
        checkPassword(user.getPassword(), requestDto.getPassword());

        List<Board> boards = boardRepository.findByUserId(user.getId());
        boards.forEach(board -> {
            board.setDeletedAt(LocalDateTime.now());
            board.getComments().forEach(Comment::delete); // 각 게시물의 댓글도 소프트 딜리트
        });
        user.setExpired(true); // 회원 탈퇴시 true로 더 이상 다른 로직이 불가하게 만듭니다.
        user.softDelete();
        userRepository.save(user);
    }

    private void checkPassword(String encryptedPassword, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, encryptedPassword)) {
            throw new IllegalArgumentException("Invalid password.");
        }
    }

    private static void getUserDetails(Long userId, User user) {
        if(!Objects.equals(userId, user.getId())){
            throw new UsernameNotFoundException(user.getUsername());
        }
    }
}
