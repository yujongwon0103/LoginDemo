package com.example.demo.config.jwt;

import com.example.demo.dao.UserDAO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {
    private final JwtProperties jwtProperties;

    /**
     * JWT 토큰 생성
     * @param user 사용자 DAO 객체
     * @param expiredAt 만료기간
     * @return JWT 토큰
     */
    public String generateToken(UserDAO user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    /**
     * <h1>JWT 토큰 생성</h1>
     * <h3>헤더(Header)</h3>
     * <p>- typ(타입) : JWT</p>
     * <h3>내용(Payload)</h3>
     * <p>- iss(발급자) : 2e40camn@gmail.com</p>
     * <p>- iat(발급일시) : 현재 시간</p>
     * <p>- exp(만료일시) : expiry</p>
     * <p>- sub(토큰제목) : username</p>
     * <p>- claim(클레임) : 유저 ID</p>
     * <h3>서명(Signature)</h3>
     * <p>- 비밀키 HS256방식 암호화</p>
     * @param expiry 만료 시간
     * @param user 유저 정보
     * @return JWT 토큰
     */
    private String makeToken(Date expiry, UserDAO user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(user.getUsername())
                .claim("id", user.getId())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    /**
     * JWT 토큰 유효성 검사
     * 토큰 복호화 진행 후 문제 없는지 판단
     * @param token JWT 토큰
     * @return true -> JWT 토큰 유효함
     */
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 토큰 기반 인증 정보 가져오기
     * @param token JWT 토큰
     * @return 인증 정보
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        User user = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(user, token, authorities);
    }

    /**
     * 토큰 기반 사용자 ID 가져오기
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    /**
     * 클레임 가져오기
     * - 토큰 복호화 후 클레임 가져옴
     * @param token JWT 토큰
     * @return 클레임
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
}
