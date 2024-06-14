package com.sparta.areadevelopment.repository;

import com.sparta.areadevelopment.entity.Like;
import com.sparta.areadevelopment.enums.LikeTypeEnum;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Like Entity 와 DB 간의 상호작용을 담당합니다.
 */
public interface LikeRepository extends JpaRepository<Like, Long> {

    /**
     * 인증을 거친 사용자 ID, 컨텐츠 ID 및 컨텐츠 타입에 따라 Like 엔티티를 찾습니다.
     *
     * @param user_id 인증을 거친 사용자 고유번호
     * @param typeId  PathVariable 로 받은 컨텐츠 고유번호
     * @param type    PathVariable 로 받은 컨텐츠 타입을 enum 으로 변환한 객체
     * @return 해당 정보가 존재한다면 Like Entity 를 반환하고, 존재하지 않다면 Null 을 반환합니다.
     */
    Optional<Like> findByUserIdAndContentIdAndContentType(Long user_id, Long typeId,
            LikeTypeEnum type);


}
