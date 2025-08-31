package com.membership.program.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.membership.program.dto.response.ErrorResponse;
import com.membership.program.service.UserService;
import com.membership.program.utility.JwtTokenUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtService;
    private final UserService userDetailsService;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtTokenUtil jwtService, UserService userDetailsService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        String username;

        try {
            username = jwtService.getUsernameFromToken(jwt);
        } catch (JwtException e) {
            log.warn("JWT validation failed: {} - Request: {}", e.getMessage(), request.getRequestURI());
            
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .errorCode("AUTH_008")
                    .message("Invalid authentication token")
                    .details("The provided JWT token is invalid or expired")
                    .path(request.getRequestURI())
                    .build();
            
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                var userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.validateToken(jwt, userDetails)) {
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    log.warn("JWT token validation failed for user: {} - Request: {}", username, request.getRequestURI());
                    
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    
                    ErrorResponse errorResponse = ErrorResponse.builder()
                            .timestamp(LocalDateTime.now())
                            .errorCode("AUTH_009")
                            .message("Token validation failed")
                            .details("The JWT token could not be validated")
                            .path(request.getRequestURI())
                            .build();
                    
                    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                    return;
                }
            } catch (Exception e) {
                log.warn("Error loading user details for JWT: {} - Request: {}", e.getMessage(), request.getRequestURI());
                
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .errorCode("AUTH_010")
                        .message("User not found")
                        .details("The user associated with this token could not be found")
                        .path(request.getRequestURI())
                        .build();
                
                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
