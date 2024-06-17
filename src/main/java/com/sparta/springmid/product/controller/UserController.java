package com.sparta.springmid.product.controller;


import com.sparta.springmid.global.security.CustomUserDetails;
import com.sparta.springmid.product.dto.PasswordChangeRequestDto;
import com.sparta.springmid.product.dto.UpdateUserDto;
import com.sparta.springmid.product.dto.UserInfoDto;
import com.sparta.springmid.product.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * DTO의 생성자 매서드
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    /**
     * DTO의 생성자 매서드
     */
    @Autowired
    private final UserService userService;

    /**
     * DTO의 생성자 매서드
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoDto> getUserProfile(
            @PathVariable("userId") Long userId
            , @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getUserProfile(userId, userDetails.getUser()));
    }

    /**
     * DTO의 생성자 매서드
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<String> updateProfile(
            @PathVariable("userId") Long userId,
            @RequestBody UpdateUserDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        userService.updateProfile(userId, requestDto, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body("Profile updated successful");
    }

    /**
     * DTO의 생성자 매서드
     */
    // redirect status code 가 있다.
    @PutMapping("/{userId}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable("userId") Long userId,
            @RequestBody PasswordChangeRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updatePassword(userId, requestDto, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body("Password changed successful");
    }

}

