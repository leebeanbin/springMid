package com.sparta.areadevelopment.config;

import com.sparta.areadevelopment.filter.JwtAuthenticationFilter;
import com.sparta.areadevelopment.jwt.TokenProvider;
import com.sparta.areadevelopment.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 시큐리티의 전반적인 설정 담은 클래스
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ComponentScan(basePackages = "com.sparta.areadevelopment.jwt")
public class SecurityConfig {

    /**
     * TokenProvider 필드값 설정
     */
    private final TokenProvider tokenProvider;

    /**
     * 암호화 매서드 빈 주입
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager 빈 주입
     *
     * @param authenticationConfiguration
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 시큐리티 필터 설정
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthService authService)
            throws Exception {
        /**
         * csrf 사용안함
         */
        http.csrf(AbstractHttpConfigurer::disable);

        /**
         *  폼을통한 로그인 방식 사용안함
         */
        http.formLogin(AbstractHttpConfigurer::disable);

        /**
         *  http basic 인증방식 사용안함
         */
        http.httpBasic(AbstractHttpConfigurer::disable);

        /**
         *  경로 별 인가 작업
         *  인증 안받는 URL
         *  "/api/auth/reissue", "/api/users/sign-up", "/api/auth/login"
         */
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/api/auth/reissue", "/api/users/sign-up", "/api/auth/login")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/boards/**").permitAll()
                .anyRequest().authenticated());

        /**
         *  시큐리티 필터보다 먼저 커스텀한 필터를 적용시킴
         */
        http.addFilterBefore(new JwtAuthenticationFilter(tokenProvider),
                UsernamePasswordAuthenticationFilter.class);

        /**
         *  로그아웃 URL시 호출성공시
         *  SecurityContextHolder를 비움
         */
        http.logout(auth -> auth
                .logoutUrl("/api/auth/logout")
                .addLogoutHandler(authService)
                .logoutSuccessHandler(
                        (((request, response, authentication) -> SecurityContextHolder.clearContext()))));
        
        /**
         * jwt를 통해 인증 인가를 할 것이기 때문에 stateless로 상태 설정
         */
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
