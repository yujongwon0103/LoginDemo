package com.example.demo.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CookieUtil {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 쿠키 추가
     * @param response 요청
     * @param name 이름
     * @param value 값
     * @param maxAge 만료 기간
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * 쿠키 삭제
     * @param request 요청
     * @param response 응답
     * @param name 이름
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) {
            return;
        }

        for(Cookie cookie : cookies) {
            if(name.equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }

    /**
     * 객체 직렬화
     * @param obj 객체
     * @return 직렬화 값
     */
    public static String serialize(Object obj) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            return Base64.getUrlEncoder().encodeToString(bytes);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 객체 역직렬화
     * @param cookie 쿠키
     * @param cls 클래스
     * @return 역직렬화 값
     */
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(cookie.getValue());
            String json = new String(bytes);
            return objectMapper.readValue(json, cls);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
