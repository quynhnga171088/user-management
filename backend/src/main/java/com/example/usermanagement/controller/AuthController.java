package com.example.usermanagement.controller;

import com.example.usermanagement.dto.AuthRequest;
import com.example.usermanagement.dto.AuthResponse;
import com.example.usermanagement.dto.UserDto;
import com.example.usermanagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling authentication and registration.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allow for dev
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user.
     *
     * @param request the UserDto containing registration details.
     * @return a ResponseEntity containing the AuthResponse.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserDto request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Authenticates a user.
     *
     * @param request the AuthRequest containing email and password.
     * @return a ResponseEntity containing the AuthResponse.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
