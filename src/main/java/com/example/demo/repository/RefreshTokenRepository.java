package com.example.demo.repository;

import com.example.demo.dao.RefreshTokenDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenDAO,Long> {
    Optional<RefreshTokenDAO> findByUserId(Long userId);
    Optional<RefreshTokenDAO> findByRefreshToken(String refreshToken);
}
