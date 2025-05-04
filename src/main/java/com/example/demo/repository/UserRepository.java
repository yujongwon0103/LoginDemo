package com.example.demo.repository;

import com.example.demo.dao.UserDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserDAO, Long> {
    /**
     * 사용자 ID로 조회
     * SELECT * FROM User WHERE email = #{email}
     * @param email 사용자 ID
     * @return 조회 성공 -> User, 조회 실패 -> null
     */
    Optional<UserDAO> findByEmail(String email);
}
