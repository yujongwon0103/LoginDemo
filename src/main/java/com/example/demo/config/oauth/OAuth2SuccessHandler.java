package com.example.demo.config.oauth;

import com.example.demo.config.jwt.TokenProvider;
import com.example.demo.dao.RefreshTokenDAO;
import com.example.demo.dao.UserDAO;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    public static final String REDIRECT_PATH = "/articles";

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository auth2AuthorizationRequestRepository;
    private final UserService userService;

    /**
     * OAuth2 인증 성공 핸들러
     * @param request 요청
     * @param response 응답
     * @param authentication 인증
     * @throws IOException 통신 예외
     * @throws ServletException 서블렛 예외
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        UserDAO user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"));

        // 리프레시 토큰 생성 -> 저장 -> 쿠키에 저장
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        saveRefreshToken(user.getId(), refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);

        // 액세스 토큰 생성 -> URL 경로에 액세스 토큰 추가
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken);

        // 인증 관련 설정값, 쿠키 제거
        clearAuthenticationAttributes(request, response);

        // 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * 생성된 리프레시 토큰을 전달받아 데이터베이스에 저장
     * @param userId 사용자 ID
     * @param newRefreshToken 생성된 리프레시 토큰
     */
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshTokenDAO refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshTokenDAO(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    /**
     * 생성된 리프레쉬 토큰을 쿠키에 저장
     * @param request 요청
     * @param response 응답
     * @param newRefreshToken 신규 리프레쉬 토큰
     */
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String newRefreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, newRefreshToken, cookieMaxAge);
    }

    /**
     * 인증 관련 설정값, 쿠키 제거
     * @param request 요청
     * @param response 응답
     */
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        auth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    /**
     * 액세스 토큰을 URL 경로에 추가
     * @param token 토큰
     * @return URL 경로
     */
    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}
