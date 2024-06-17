package com.sparta.springmid.product.repository;


import com.sparta.springmid.product.model.ApiUseTime;
import com.sparta.springmid.product.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiUseTimeRepository extends JpaRepository<ApiUseTime, Long> {
    Optional<ApiUseTime> findByUser(User user);
}