package com.sparta.areadevelopment.entity;

import com.sparta.areadevelopment.enums.LikeTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 좋아요(Like) 엔티티 클래스. 사용자가 게시물이나 댓글에 대해 좋아요를 누른 정보를 DB에 저장합니다.
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "likes")
public class Like {

    /**
     * 좋아요의 고유번호.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 좋아요를 누른 컨텐츠 타입의 고유번호.
     */
    @Column(nullable = false)
    private Long contentId;

    /**
     * 좋아요를 누른 컨텐츠 타입.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LikeTypeEnum contentType;

    /**
     * 좋아요를 누를 시간.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * 좋아요를 누른 사용자 객체필드입니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Like Entity 의 생성자 입니다.
     *
     * @param user        사용자 객체
     * @param contentId   컨텐츠 타입 고유번호
     * @param contentType 컨텐츠 타입
     */
    public Like(User user, Long contentId, LikeTypeEnum contentType) {
        this.user = user;
        this.contentId = contentId;
        this.contentType = contentType;
        this.createdAt = LocalDateTime.now();
    }
}
