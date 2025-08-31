package com.membership.program.service.implementation;

import com.membership.program.dto.request.UserRequestDTO;
import com.membership.program.dto.response.UserResponseDTO;
import com.membership.program.entity.User;
import com.membership.program.dto.enums.UserStatus;
import com.membership.program.exception.MembershipException;
import com.membership.program.mapper.UserMapper;
import com.membership.program.repository.UserRepository;
import com.membership.program.service.UserService;
import com.membership.program.utility.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextUtil securityContextUtil;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO dto) {
        log.info("Creating new user with username: {}", dto.getUsername());
        
        try {
            // Check if username already exists
            if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
                throw new MembershipException("Username '" + dto.getUsername() + "' already exists");
            }

            // Check if email already exists
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new MembershipException("Email '" + dto.getEmail() + "' already exists");
            }

            User user = UserMapper.fromDto(dto);
            user.setPassword(passwordEncoder.encode(dto.getPassword()));

            // Set default role if none provided
            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                user.setRoles(Set.of("ROLE_USER"));
            }

            // Set default security settings
            user.setEnabled(true);
            user.setAccountNonLocked(true);
            user.setAccountNonExpired(true);
            user.setCredentialsNonExpired(true);
            user.setStatus(UserStatus.ACTIVE);

            User savedUser = userRepository.save(user);
            log.info("Successfully created user with ID: {}", savedUser.getId());

            return UserMapper.toDto(savedUser);
            
        } catch (MembershipException e) {
            log.warn("Failed to create user {}: {}", dto.getUsername(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating user {}: {}", dto.getUsername(), e.getMessage(), e);
            throw new MembershipException("Failed to create user: " + e.getMessage(), e);
        }
    }

    @Override
    public UserResponseDTO getCurrentUser() {
        try {
            User user = securityContextUtil.getCurrentUser();
            log.debug("Retrieved current user: {}", user.getUsername());
            return UserMapper.toDto(user);
        } catch (Exception e) {
            log.error("Error retrieving current user: {}", e.getMessage(), e);
            throw new MembershipException("Failed to retrieve current user: " + e.getMessage(), e);
        }
    }
}
