package com.example.demo.service;

import com.example.demo.dto.SaveUserDTO;
import com.example.demo.dao.UserDAO;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Long save(SaveUserDTO dto) {
        UserDAO user = UserDAO.builder()
                .username(dto.getUsername())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .build();
        return userRepository.save(user).getId();
    }
}
