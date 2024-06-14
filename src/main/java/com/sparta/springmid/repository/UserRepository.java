package com.sparta.areadevelopment.repository;

import com.sparta.areadevelopment.entity.User;
import com.sparta.areadevelopment.enums.StatusEnum;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


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
