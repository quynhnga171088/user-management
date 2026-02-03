package com.example.usermanagement.service;

import com.example.usermanagement.dto.AuthRequest;
import com.example.usermanagement.dto.AuthResponse;
import com.example.usermanagement.dto.UserDto;
import com.example.usermanagement.exception.BusinessException;
import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.util.Set;

/**
 * Service for handling authentication and registration.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtils jwtUtils;
        private final AuthenticationManager authenticationManager;
        private final CustomUserDetailsService userDetailsService;

        /**
         * Registers a new user.
         *
         * @param request the UserDto containing registration details.
         * @return an AuthResponse containing the JWT token.
         * @throws BusinessException if the email already exists.
         */
        @Transactional
        public AuthResponse register(UserDto request) {
                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new BusinessException("Email already exists");
                }

                var user = User.builder()
                                .name(request.getName())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .roles(request.getRoles() != null && !request.getRoles().isEmpty() ? request.getRoles()
                                                : Set.of(Role.USER))
                                .active(true)
                                .createDate(LocalDateTime.now())
                                .build();

                userRepository.save(user);

                // Auto login is optional, for now just return generated token or null, usually
                // login required
                // But let's return a token for convenience
                UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
                var jwtToken = jwtUtils.generateToken(userDetails);

                return AuthResponse.builder()
                                .token(jwtToken)
                                .name(user.getName())
                                .email(user.getEmail())
                                .role(user.getRoles().stream().findFirst().map(Enum::name).orElse("USER"))
                                .build();
        }

        /**
         * Authenticates a user.
         *
         * @param request the AuthRequest containing email and password.
         * @return an AuthResponse containing the JWT token.
         * @throws BusinessException if authentication fails or user is not found.
         */
        public AuthResponse login(AuthRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));
                var user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new BusinessException("User not found"));
                UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
                var jwtToken = jwtUtils.generateToken(userDetails);

                return AuthResponse.builder()
                                .token(jwtToken)
                                .name(user.getName())
                                .email(user.getEmail())
                                .role(user.getRoles().stream().findFirst().map(Enum::name).orElse("USER"))
                                .build();
        }
}
