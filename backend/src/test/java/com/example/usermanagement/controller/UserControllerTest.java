package com.example.usermanagement.controller;

import com.example.usermanagement.dto.UserDto;
import com.example.usermanagement.security.JwtAuthenticationFilter;
import com.example.usermanagement.service.CustomUserDetailsService;
import com.example.usermanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("Test User 01")
                .email("test@example.com")
                .active(true)
                .build();
    }

    @Test
    void getAllUsers_Success() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    void getUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void createUser_Success() throws Exception {
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User 01"));
    }

    @Test
    void updateUser_Success() throws Exception {
        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(put("/api/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User 01"));
    }

    @Test
    void deleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
