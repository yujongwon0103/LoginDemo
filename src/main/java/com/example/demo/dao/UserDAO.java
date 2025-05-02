package com.example.demo.dao;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class UserDAO implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @CreatedDate // 엔티티가 생성될 때 생성 시간 저장
    @Column(name = "created_dt")
    private LocalDateTime createdDt;

    @LastModifiedDate // 엔티티가 수정될 때 수정 시간 저장
    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;
    
    @Builder
    public UserDAO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * 사용자 권한 리스트 반환
     * @return List<SimpleGrantedAuthority>({"role":"user"})
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    /**
     * 사용자 아이디 반환 (고유값)
     * @return username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * 사용자 패스워드 반환 (암호화)
     * @return password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * 계정 만료 여부 반환
     * @return true -> 만료되지 않았음
     */
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    /**
     * 계정 잠금 여부 반환
     * @return true -> 잠금되지 않았음
     */
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    /**
     * 패스워드 만료 여부 반환
     * @return true -> 만료되지 않았음
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    /**
     * 계정 사용 가능 여부 반환
     * @return true -> 사용 가능
     */
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
