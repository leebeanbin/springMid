package com.sparta.areadevelopment.service;

import com.sparta.areadevelopment.entity.Board;
import com.sparta.areadevelopment.entity.Comment;
import com.sparta.areadevelopment.entity.Like;
import com.sparta.areadevelopment.entity.User;
import com.sparta.areadevelopment.enums.LikeTypeEnum;
import com.sparta.areadevelopment.repository.BoardRepository;
import com.sparta.areadevelopment.repository.CommentRepository;
import com.sparta.areadevelopment.repository.LikeRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * 이 Service 는 좋아요 기능의 비지니스 로직을 담당합니다.
 */
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    /**
     * 지정된 Repository 로 Service 를 생성합니다.
     *
     * @param likeRepository    Like Entity 의 저장장소
     * @param boardRepository   Board Entity 의 저장장소
     * @param commentRepository Comment Entity 의 저장장소
     */
    public LikeService(LikeRepository likeRepository, BoardRepository boardRepository,
            CommentRepository commentRepository) {
        this.likeRepository = likeRepository;
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * 지정된 사용자가 선탠한 컨텐츠에 대해 좋아요 내역이 없다면 추가, 정보가 있다면 삭제합니다.
     *
     * @param user        좋아요를 누른 사용자 객체
     * @param contentType 좋아요를 누른 컨텐츠 타입
     * @param contentId   좋아요를 누른 컨텐츠의 고유 번호
     * @return 좋아요 등록 : true 좋아요 취소 : false
     */
    @Transactional
    public boolean toggleLike(User user, String contentType, Long contentId) {
        LikeTypeEnum likeType = LikeTypeEnum.fromContentType(contentType);
        Optional<Like> checkLike = likeRepository.findByUserIdAndContentIdAndContentType(
                user.getId(), contentId, likeType);
        if (checkLike.isPresent()) {
            likeRepository.delete(checkLike.get());
            decrementLikeCount(contentId, contentType);
            return false; // 좋아요 취소
        } else {
            validateContentUser(user.getId(), contentType, contentId);
            Like like = new Like(user, contentId, likeType);
            likeRepository.save(like);
            incrementLikeCount(contentId, contentType);
            return true; // 좋아요 등록
        }
    }

    /**
     * 좋아요를 누른 사용자가 컨텐츠의 작성자인지 확인하는 기능입니다.
     *
     * @param userId      좋아요를 누른 사용자 고유번호
     * @param contentType 좋아요를 누른 컨텐츠 타입
     * @param contentId   좋아요를 누른 컨텐츠의 고유 번호
     */
    private void validateContentUser(Long userId, String contentType, Long contentId) {

        // 객체를 가져옵니다
        if (LikeTypeEnum.BOARD.equalsType(contentType)) {
            Board board = boardRepository.findById(contentId)
                    .orElseThrow(() -> new IllegalArgumentException("해당하는 일정이 없습니다."));
            checkContentOwners(board.getUser().getId(), userId);
        } else if (LikeTypeEnum.COMMENT.equalsType(contentType)) {
            Comment comment = commentRepository.findById(contentId)
                    .orElseThrow(() -> new IllegalArgumentException("해당하는 일정이 없습니다."));
            checkContentOwners(comment.getUser().getId(), userId);
        } else {
            throw new IllegalArgumentException("지원하지 않는 타입입니다.");
        }
    }

    /**
     * 사용자와 컨텐츠의 작성자를 비교하는 로직입니다.
     *
     * @param OwnerId 컨텐츠 작성자의 유저 고유번호입니다.
     * @param userId  사용자의 유저 고유번호입니다.
     */
    private void checkContentOwners(Long OwnerId, Long userId) {
        if (OwnerId.equals(userId)) {
            throw new IllegalArgumentException("본인이 작성한 컨텐츠에는 좋아요를 남길 수 없습니다.");
        }
    }

    /**
     * 조건에 따라 해당 저장소에 좋아요 필드 숫자 증가 메서드입니다.
     *
     * @param contentId   컨텐츠 고유 번호
     * @param contentType 컨텐츠 타입
     */
    private void incrementLikeCount(Long contentId, String contentType) {
        if (LikeTypeEnum.BOARD.equalsType(contentType)) { // 게시판 일때
            boardRepository.incrementLikeCount(contentId);
        } else if (LikeTypeEnum.COMMENT.equalsType(contentType)) { // 댓글 일때
            commentRepository.incrementLikeCount(contentId);
        } else {
            throw new IllegalArgumentException("지원하지 않는 타입입니다.");
        }
    }

    /**
     * 조건에 따라 해당 저장소에 좋아요 숫자 감소 메서드입니다.
     *
     * @param contentId   컨텐츠 고유 번호
     * @param contentType 컨텐츠 타입
     */
    private void decrementLikeCount(Long contentId, String contentType) {
        if (LikeTypeEnum.BOARD.equalsType(contentType)) { // 게시판 일때
            boardRepository.decrementLikeCount(contentId);
        } else if (LikeTypeEnum.COMMENT.equalsType(contentType)) { // 댓글 일때
            commentRepository.decrementLikeCount(contentId);
        } else {
            throw new IllegalArgumentException("지원하지 않는 타입입니다.");
        }
    }
}
