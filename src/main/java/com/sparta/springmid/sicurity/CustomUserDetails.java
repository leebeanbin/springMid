package com.sparta.areadevelopment.entity;

import java.util.ArrayList;
import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


/**
 * 유저디테일을 커스텀 한 클래스
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    /**
     * 생성자 매서드
     * @param user
     */
    public CustomUserDetails(User user) {
        this.user = user;
    }
    /**
     * 해당 유저의 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(() -> "ROLE_USER");
        return authorities;
    }

    /**
     * 비밀번호
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }


    /**
     * 아이디
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * 계정 만료 여부 true : 만료 안됨 false : 만료
     *
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠김 여부 true : 잠기지 않음 false : 잠김
     *
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 비밀번호 만료 여부 true : 만료 안됨 false : 만료
     *
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    /**
     * 사용자 활성화 여부 ture : 활성화 false : 비활성화
     *
     * @return
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

}

