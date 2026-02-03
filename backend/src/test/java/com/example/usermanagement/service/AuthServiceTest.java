package com.example.usermanagement.service;

import com.example.usermanagement.dto.AuthRequest;
import com.example.usermanagement.dto.AuthResponse;
import com.example.usermanagement.dto.UserDto;
import com.example.usermanagement.exception.BusinessException;
import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .roles(Set.of(Role.USER))
                .build();

        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .roles(Set.of(Role.USER))
                .build();
    }

    @Test
    void register_Success() {
        when(userRepository.existsByEmail(any(String.class))).thenReturn(false);
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userDetailsService.loadUserByUsername(any(String.class))).thenReturn(mock(UserDetails.class));
        when(jwtUtils.generateToken(any(UserDetails.class))).thenReturn("jwtToken");

        AuthResponse response = authService.register(userDto);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals("test@example.com", response.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_EmailAlreadyExists() {
        when(userRepository.existsByEmail(any(String.class))).thenReturn(true);

        assertThrows(BusinessException.class, () -> authService.register(userDto));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        AuthRequest authRequest = new AuthRequest("test@example.com", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername(any(String.class))).thenReturn(mock(UserDetails.class));
        when(jwtUtils.generateToken(any(UserDetails.class))).thenReturn("jwtToken");

        AuthResponse response = authService.login(authRequest);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    void login_UserNotFound() {
        AuthRequest authRequest = new AuthRequest("test@example.com", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> authService.login(authRequest));
    }
}
