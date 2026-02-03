package com.example.usermanagement.dto;

import com.example.usermanagement.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String password; // Only for request, handle carefully
    private Set<Role> roles;
    private LocalDateTime createDate;
    private Boolean active;
}
