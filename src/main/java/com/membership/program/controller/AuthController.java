package com.membership.program.controller;

import com.membership.program.dto.request.LoginRequest;
import com.membership.program.dto.response.LoginResponse;
import com.membership.program.entity.User;
import com.membership.program.exception.UserDisableException;
import com.membership.program.repository.UserRepository;
import com.membership.program.service.UserService;
import com.membership.program.utility.JwtTokenUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    public final AuthenticationManager authenticationManager;
    public final UserService userService;
    public final UserRepository userRepository;
    public final JwtTokenUtil jwtTokenUtil;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtTokenUtil jwtTokenUtil, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        try {
            // Authenticate user
            authenticate(loginRequest.getUserName(), loginRequest.getPassWord());
            
            // Load user details and generate token
            User userResponse = this.userService.loadUserByUsername(loginRequest.getUserName());
            String token = jwtTokenUtil.generateToken(userResponse);
            
            // Update last login time
            userResponse.updateLastLogin();
            userRepository.save(userResponse);
            
            log.info("User {} successfully logged in", loginRequest.getUserName());
            
            LoginResponse loginResponse = new LoginResponse(loginRequest.getUserName(), token);
            return ResponseEntity.ok(loginResponse);
            
        } catch (UserDisableException e) {
            log.warn("Login failed for user {}: Account disabled", loginRequest.getUserName());
            throw e; // Let GlobalExceptionHandler handle this
        } catch (BadCredentialsException e) {
            log.warn("Login failed for user {}: Invalid credentials", loginRequest.getUserName());
            throw e; // Let GlobalExceptionHandler handle this
        } catch (Exception e) {
            log.error("Unexpected error during login for user {}: {}", loginRequest.getUserName(), e.getMessage(), e);
            throw e; // Let GlobalExceptionHandler handle this
        }
    }

    private void authenticate(String userName, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
        } catch (DisabledException e) {
            log.warn("Authentication failed for user {}: Account disabled", userName);
            throw new UserDisableException(e);
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for user {}: Bad credentials", userName);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected authentication error for user {}: {}", userName, e.getMessage(), e);
            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
        }
    }
}
