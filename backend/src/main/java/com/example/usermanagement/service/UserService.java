package com.example.usermanagement.service;

import com.example.usermanagement.dto.UserDto;
import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserDto createUser(UserDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(request.getRoles() != null ? request.getRoles() : Set.of(Role.USER))
                .active(request.getActive() != null ? request.getActive() : true)
                .createDate(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    public UserDto updateUser(Long id, UserDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRoles() != null) {
            user.setRoles(request.getRoles());
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles())
                .createDate(user.getCreateDate())
                .active(user.getActive())
                .build();
    }
}
