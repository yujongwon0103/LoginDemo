package com.example.demo.config.oauth;

import com.example.demo.dao.UserDAO;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    /**
     * 요청에 의한 유저 정보 불러오기
     * @param userRequest 유저 정보 요청
     * @return 유저 정보
     * @throws OAuth2AuthenticationException OAuth2 인증 예외
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        saveOrUpdate(user);
        return user;
    }

    /**
     * 유저가 있으면 업데이트, 없으면 유저 생성
     * @param oAuth2User 인증된 유저
     * @return 생성/업데이트 된 유저
     */
    private UserDAO saveOrUpdate(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        UserDAO user = userRepository.findByEmail(email)
                .map(entity -> entity.update(name))
                .orElse(UserDAO.builder().email(email).nickname(name).build());

        return userRepository.save(user);
    }
}
