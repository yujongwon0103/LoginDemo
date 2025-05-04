package com.example.demo.util;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class CookieUtilTest {

    @Test
    void addCookie() {
    }

    @Test
    void deleteCookie() {
    }

    class TestObj {
        private int num;
        private String str;
        public TestObj() {}

        public int getNum() { return num; }
        public String getStr() { return str; }
        public void setNum(int num) { this.num = num; }
        public void setStr(String str) { this.str = str; }
    }

    @Test
    void serialize() {
        // case
        TestObj obj = new TestObj();
        obj.setNum(1);
        obj.setStr("일");

        // when
        String str = CookieUtil.serialize(obj);

        // then
        assertThat(str).isNotEmpty();

    }

    @Test
    void deserialize() {
        // case
        Cookie cookie = new Cookie("name", "value");

        // when
        TestObj obj = CookieUtil.deserialize(cookie, TestObj.class);

        // then
        assertThat(obj).isNotExactlyInstanceOf(TestObj.class);
    }
}