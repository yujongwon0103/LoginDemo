package com.example.demo.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SaveUserDTOTest {

    @Test
    void getUsername() {
        SaveUserDTO dto = new SaveUserDTO();
        dto.setUsername("YJW");
        assertEquals("YJW", dto.getUsername());
    }

    @Test
    void getPassword() {
        SaveUserDTO dto = new SaveUserDTO();
        dto.setPassword("1234");
        assertEquals("1234", dto.getPassword());
    }

    @Test
    void setUsername() {
        SaveUserDTO dto = new SaveUserDTO();
        dto.setUsername("YJW");
        assertEquals("YJW", dto.getUsername());
    }

    @Test
    void setPassword() {
        SaveUserDTO dto = new SaveUserDTO();
        dto.setPassword("1234");
        assertEquals("1234", dto.getPassword());
    }
}