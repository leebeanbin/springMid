package com.sparta.springmid.product.repository;

import com.sparta.springmid.global.enums.StatusEnum;
import com.sparta.springmid.product.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * 유저 레포지토리
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 유저를 찾는 쿼리문
     *
     * @param username
     * @return
     */
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Optional<User> findByRefreshToken(String token);

    Optional<User> findUserByUsernameAndStatus(String username, StatusEnum statusEnum);
}
