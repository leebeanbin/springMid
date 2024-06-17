package com.sparta.springmid.global.aop;


import com.sparta.springmid.global.security.CustomUserDetails;
import com.sparta.springmid.product.model.ApiUseTime;
import com.sparta.springmid.product.model.User;
import com.sparta.springmid.product.repository.ApiUseTimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j(topic = "UseTimeAop")
@Aspect
@Component
@RequiredArgsConstructor
public class UseTimeAop {

    private final ApiUseTimeRepository apiUseTimeRepository;

    @Pointcut("execution(* com.sparta.springmid.product.controller.AuthController.*(..))")
    private void auth() {
    }

    @Pointcut("execution(* com.sparta.springmid.product.controller.UserController.*(..))")
    private void user() {
    }

    @Pointcut("execution(* com.sparta.springmid.product.controller.BoardController.*(..))")
    private void board() {
    }

    @Pointcut("execution(* com.sparta.springmid.product.controller.CommentController.*(..))")
    private void comment() {
    }


    @Around("auth() || user() || board() || comment()") // 포인트 값
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        // 측정 시작 시간
        long startTime = System.currentTimeMillis();

        try {
            // 핵심기능 수행
            Object output = joinPoint.proceed();
            return output;
        } finally {
            // 측정 종료 시간
            long endTime = System.currentTimeMillis();
            // 수행시간 = 종료 시간 - 시작 시간
            long runTime = endTime - startTime;

            // 로그인 회원이 없는 경우, 수행시간 기록하지 않음
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal().getClass() == CustomUserDetails.class) {
                // 로그인 회원 정보
                CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                User loginUser = userDetails.getUser();

                // API 사용시간 및 DB 에 기록
                ApiUseTime apiUseTime = apiUseTimeRepository.findByUser(loginUser).orElse(null);
                if (apiUseTime == null) {
                    // 로그인 회원의 기록이 없으면
                    apiUseTime = new ApiUseTime(loginUser, runTime);
                } else {
                    // 로그인 회원의 기록이 이미 있으면
                    apiUseTime.addUseTime(runTime);
                }

                log.info("[API Use Time] Username: " + loginUser.getUsername() + ", Total Time: "
                        + apiUseTime.getTotalTime() + " ms");
                apiUseTimeRepository.save(apiUseTime);
            }
        }
    }
}