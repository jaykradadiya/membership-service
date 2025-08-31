package com.membership.program.utility;

import com.membership.program.entity.User;
import com.membership.program.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Utility class for accessing authenticated user information from SecurityContext
 * Provides convenient methods to get current user details without repetitive SecurityContext code
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityContextUtil {

    private final UserRepository userRepository;

    /**
     * Get the current authenticated username
     * 
     * @return username of the authenticated user
     * @throws IllegalStateException if no user is authenticated
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found in SecurityContext");
        }
        
        return authentication.getName();
    }

    /**
     * Get the current authenticated user entity
     * 
     * @return User entity of the authenticated user
     * @throws UsernameNotFoundException if user not found in database
     * @throws IllegalStateException if no user is authenticated
     */
    public User getCurrentUser() {
        String username = getCurrentUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * Get the current authenticated user entity as Optional
     * 
     * @return Optional containing User entity if authenticated, empty otherwise
     */
    public Optional<User> getCurrentUserOptional() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }
            
            String username = authentication.getName();
            return userRepository.findByUsername(username);
        } catch (Exception e) {
            log.warn("Error getting current user: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Get the current authenticated user ID
     * 
     * @return ID of the authenticated user
     * @throws UsernameNotFoundException if user not found in database
     * @throws IllegalStateException if no user is authenticated
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Get the current authenticated user ID as Optional
     * 
     * @return Optional containing user ID if authenticated, empty otherwise
     */
    public Optional<Long> getCurrentUserIdOptional() {
        return getCurrentUserOptional().map(User::getId);
    }

    /**
     * Check if a user is currently authenticated
     * 
     * @return true if user is authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication != null && authentication.isAuthenticated();
        } catch (Exception e) {
            log.warn("Error checking authentication status: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if the current user has a specific role
     * 
     * @param role the role to check
     * @return true if user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }
            
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role.toUpperCase()) ||
                                         authority.getAuthority().equals(role.toUpperCase()));
        } catch (Exception e) {
            log.warn("Error checking role {}: {}", role, e.getMessage());
            return false;
        }
    }

    /**
     * Check if the current user is an admin
     * 
     * @return true if user is admin, false otherwise
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Check if the current user is a regular user
     * 
     * @return true if user has USER role, false otherwise
     */
    public boolean isUser() {
        return hasRole("USER");
    }

    /**
     * Get the current user's tier level
     * 
     * @return tier level of the authenticated user
     * @throws UsernameNotFoundException if user not found in database
     * @throws IllegalStateException if no user is authenticated
     */
    public Integer getCurrentUserTierLevel() {
        return getCurrentUser().getCurrentTierLevel();
    }

    /**
     * Get the current user's cohort
     * 
     * @return cohort of the authenticated user
     * @throws UsernameNotFoundException if user not found in database
     * @throws IllegalStateException if no user is authenticated
     */
    public String getCurrentUserCohort() {
        return getCurrentUser().getCohort();
    }

    /**
     * Safely get current user information without throwing exceptions
     * Useful for logging or non-critical operations
     * 
     * @return UserInfo object containing user details, or null if not available
     */
    public UserInfo getCurrentUserInfo() {
        try {
            User user = getCurrentUser();
            return UserInfo.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .tierLevel(user.getCurrentTierLevel())
                    .cohort(user.getCohort())
                    .build();
        } catch (Exception e) {
            log.debug("Could not get current user info: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Inner class to hold basic user information for safe access
     */
    @lombok.Data
    @lombok.Builder
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private Integer tierLevel;
        private String cohort;
    }
}
