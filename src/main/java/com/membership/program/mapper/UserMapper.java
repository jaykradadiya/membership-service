package com.membership.program.mapper;

import com.membership.program.dto.request.UserRequestDTO;
import com.membership.program.dto.response.UserResponseDTO;
import com.membership.program.entity.User;

public class UserMapper {
    public static UserResponseDTO toDto(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .failedLoginAttempts(user.getFailedLoginAttempts())
                .accountLockedUntil(user.getAccountLockedUntil())
                .roles(user.getRoles())
                .accountNonExpired(user.isAccountNonExpired())
                .accountNonLocked(user.isAccountNonLocked())
                .credentialsNonExpired(user.isCredentialsNonExpired())
                .enabled(user.isEnabled())
                .build();
    }

    public static User fromDto(UserRequestDTO dto) {
        return User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword()) // Important: Encode password before saving!
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .roles(dto.getRoles())
                .build();
    }
}
