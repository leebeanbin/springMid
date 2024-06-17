//package com.sparta.springmid.global.logging;
//
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.annotation.AfterReturning;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//
//@Aspect
//@Component
//@Slf4j
//public class LoggingAspect {
//
//    private final HttpServletRequest request;
//
//    public LoggingAspect(HttpServletRequest request) {
//        this.request = request;
//    }
//
//    // RestController 안에 모든 메서드에 다 적용
//    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
//    public void controller() {}
//
//    @AfterReturning("controller()")
//    public void logAfter() {
//        String method = request.getMethod();
//        String url = request.getRequestURL().toString();
//        log.info("Request URL: {}, HTTP Method: {}", url, method);
//    }
//}