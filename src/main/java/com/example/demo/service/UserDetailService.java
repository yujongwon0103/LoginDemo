package com.example.demo.service;

import com.example.demo.dao.UserDAO;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 사용자 ID로 사용자 정보 불러오기
     * @param username 사용자 ID
     * @return User 사용자 정보
     * @throws UsernameNotFoundException 사용자 ID 값이 없는 경우 예외처리
     */
    @Override
    public UserDAO loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(()->new IllegalArgumentException(username));
    }
}
