package com.example.demo.config;

import com.example.demo.config.jwt.TokenProvider;
import com.example.demo.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.demo.config.oauth.OAuth2SuccessHandler;
import com.example.demo.config.oauth.OAuth2UserCustomService;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.service.UserDetailService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

//    private final UserDetailService userDetailService;

    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    /**
     * 스프링 시큐리티 모든 기능 비활성화
     * - h2-console
     * - 정적 리소스
     * @return WebSecurityCustomizer
     */
//    @Bean
//    public WebSecurityCustomizer configure() {
//        return (web) -> web.ignoring()
//                .requestMatchers(toH2Console())
//                .requestMatchers(new AntPathRequestMatcher("/static/**"));
//    }

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers(
                        new AntPathRequestMatcher("/img/**"),
                        new AntPathRequestMatcher("/css/**"),
                        new AntPathRequestMatcher("/js/**")
                );
    }

    /**<h1>특정 HTTP 요청에 대한 웹 기반 보안 구성</h1>
     * <br>
     * <p>- 인증/인가</p>
     * <p>1. 특정 요청과 일치하는 URL 엑세스: ["/login", "/signup", "/user"]</p>
     * <p>2. 모든 권한(인증/인가 없이도) 접근 가능</p>
     * <p>3. 이외의 요청은</p>
     * <p>4. 별도의 인가는 필요없지만 인증이 성공된 상태여야 접근 가능</p>
     * <br>
     * <p>- 로그인</p>
     * <p>1. 로그인 화면 URL: /login</p>
     * <p>2. 로그인 성공 시 이동 URL: /articles</p>
     * <br>
     * <p>- 로그아웃</p>
     * <p>1. 로그아웃 성공 시 이동 URL: /login</p>
     * <p>2. 로그아웃 이후 세션 전체 삭제 여부: true</p>
     * <br>
     * <p>- CSRF 설정</p>
     * <p>1. 비활성화 (임시)</p>
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 모든 예외
     */
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http.authorizeHttpRequests((authorize) -> authorize.requestMatchers(
//                        new AntPathRequestMatcher("/login"),
//                        new AntPathRequestMatcher("/signup"),
//                        new AntPathRequestMatcher("/user")
//                ).permitAll()
//                        .anyRequest()
//                        .authenticated())
//                .formLogin((formLogin) -> formLogin
//                        .loginPage("/login")
//                        .defaultSuccessUrl("/home")
//                ).logout((logout) -> logout
//                        .logoutSuccessUrl("/login")
//                        .invalidateHttpSession(true)
//                )
//                .csrf(AbstractHttpConfigurer::disable)
//                .build();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(new AntPathRequestMatcher("/api/token")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/**")).authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint
                                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                        )
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(oAuth2UserCustomService)
                        )
                        .successHandler(oAuth2SuccessHandler())
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/api/**")
                        )
                )
                .build();
    }

    /**
     * 인증 관리자 관련 설정
     * - UserDetailService 재정의
     * - 인증 방법(ex. LDAP, JDBC 기반 인증 등) 설정
     * @return AuthenticationManager
     */
//    @Bean
//    public AuthenticationManager authenticationManager() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailService);
//        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
//        return new ProviderManager(authProvider);
//    }

    /**
     * 패스워드 인코더 빈 등록
     * @return BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(
                tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService
        );
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }
}
