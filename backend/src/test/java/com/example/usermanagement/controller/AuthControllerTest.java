package com.example.usermanagement.controller;

import com.example.usermanagement.dto.AuthRequest;
import com.example.usermanagement.dto.AuthResponse;
import com.example.usermanagement.dto.UserDto;
import com.example.usermanagement.security.JwtAuthenticationFilter;
import com.example.usermanagement.service.AuthService;
import com.example.usermanagement.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for unit testing logic
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        authResponse = AuthResponse.builder()
                .token("jwtToken")
                .name("Test User")
                .email("test@example.com")
                .role("USER")
                .build();
    }

    @Test
    void register_Success() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();

        when(authService.register(any(UserDto.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwtToken"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void login_Success() throws Exception {
        AuthRequest authRequest = new AuthRequest("test@example.com", "password");

        when(authService.login(any(AuthRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwtToken"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}
