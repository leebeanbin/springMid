package com.sparta.areadevelopment.controller;

import com.sparta.areadevelopment.dto.LikeDto;
import com.sparta.areadevelopment.entity.CustomUserDetails;
import com.sparta.areadevelopment.service.LikeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 이 Controller 는 게시글과 댓글에 연동된 좋아요 요청을 받고 결과를 반환합니다.
 */
@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    /**
     * 지정된 서비스로 LikeController 를 생성합니다.
     *
     * @param likeService 좋아요 기능의 비지니스 로직 구현 계층
     */
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    /**
     * 사용자의 입력을 받아 좋아요 내역이 없으면 등록하고 내역이 있다면 취소합니다.
     *
     * @param userDetails 시큐리티 인증을 통과한 유저의 정보
     * @param likeDto     좋아요가 적용 되야할 타입과 고유번호
     * @return 상태코드 200과 새로운 좋아요가 추가되면 true, 기존 좋아요가 제거되면 false
     */
    @PostMapping
    public ResponseEntity<String> likeContent(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody LikeDto likeDto) {

        boolean isLiked = likeService.toggleLike(userDetails.getUser(),
                likeDto.getContentType().toLowerCase(), likeDto.getContentId());
        if (isLiked) {
            return ResponseEntity.ok("좋아요 등록 성공");
        } else {
            return ResponseEntity.ok("좋아요 취소 성공");
        }
    }
}

